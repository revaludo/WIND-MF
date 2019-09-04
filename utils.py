# coding=utf-8
import pymysql
db = pymysql.connect('localhost', 'root', 'Zaoldyck', 'dataset', 3306)
cursor = db.cursor()


# divide the visits of users into train, valid, and test, and label them
def labeling():
    sql = "SELECT distinct(userid) FROM usersbyv_PDBSCAN"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            userid = row[0]
            sql = "SELECT userid,city FROM usersbyv_PDBSCAN where userid='%s' order BY visittime desc" % userid
            try:
                cursor.execute(sql)
                results2 = cursor.fetchall()
                for i in range(len(results2)):
                    userid = results2[i][0]
                    city = results2[i][1]
                    if i == 0:
                        label = 'test'
                    else:
                        if i == 1:
                            label = 'valid'
                        else:
                            label = 'train'
                    sql = "update usersbyv_PDBSCAN set label='%s' where userid='%s' and city='%s'" % (
                    label, userid, city)
                    try:
                        cursor.execute(sql)
                        db.commit()
                    except Exception as e:
                        db.rollback()
                        print(e)
            except Exception as e:
                db.rollback()
                print(e)
    except Exception as e:
        db.rollback()
        print (e)


# Extract Visit Sequences given certain time threshold
def getsequences(timeinterval):
    dict = {}
    sql = "SELECT distinct(userid) FROM usersbyv where label='train'"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            dict[row[0]] = []
    except Exception as e:
        db.rollback()
        print(e)
    output=open('data/visit_sequences_'+str(timeinterval)+'h.txt','w')
    sql = "SELECT userid,city FROM usersbyv where label='train'"
    try:
            cursor.execute(sql)
            results = cursor.fetchall()
            for row in results:
                userid = row[0]
                city = row[1]
                sql = "SELECT poiid,visittime FROM visit_china where userid='%s'and city='%s' order by visittime" % (
                userid, city)
                try:
                    cursor.execute(sql)
                    results2 = cursor.fetchall()
                    tms = []
                    pois=[]
                    for row2 in results2:
                        poiid=row2[0]
                        visittime = row2[1]
                        pois.append(poiid)
                        tms.append(visittime)
                        # print ("user=%s,poi=%d,takentime=%d,city=%s" % \
                        #        (userid,poiid,takentime,city))

                    start = 0
                    for m in range(len(tms) - 1):
                        if (int(tms[m + 1]) - int(tms[m])) > timeinterval * 3600:
                            s1=''
                            for j in range(start, m + 1):
                                s1+=str(pois[j])+','
                            dict[userid].append(s1[:-1])
                            start = m + 1


                    s1 = ''
                    for j in range(start, len(tms)):
                        s1 += str(pois[j]) + ','
                    dict[userid].append(s1[:-1])


                except Exception as e:
                    db.rollback()
                    print(e)
    except Exception as e:
            db.rollback()
            print (e)
    for user in dict:
        output.write(user + ':' + ';'.join(dict[user]) + '\n')
    output.close()



# Sequential Information Modeling with Doc2vec
import gensim
from gensim import models,corpora
import re
def read_corpus(fname):
    for line in open(fname, encoding="utf-8"):
        # print(line)        # For training data, add
        yield models.doc2vec.TaggedDocument(words=re.split('[,;]', line.strip().split(':')[1]),
                                            tags=line.strip().split(':')[0])


def d2v(timeinterval):
    corpus = list(read_corpus('data/visit_sequences_' + str(timeinterval) + 'h.txt'))
    # print(corpus[:2])
    model = models.doc2vec.Doc2Vec(vector_size=50, min_count=1, epochs=40)  # use fixed learning rate
    model.build_vocab(corpus)
    # print(model.infer_vector(corpus[0].words))
    model.save('data/' + str(timeinterval) + '.model')
    output = open('data/poiseq_' + str(timeinterval) + '.txt', 'w')
    for i in range(1, 1515):
        try:
            vector = model[str(i)]
        except:
            vector = [0 for i in range(50)]
        s = str(i) + ':'
        for j in range(50):
            s += str(vector[j]) + '\t'
        output.write(s[:-1] + '\n')
    output.close()

    output = open('data/userseq_' + str(timeinterval) + '.txt', 'w')
    for user in corpus:
        # print(user.tags)
        vector = (model.infer_vector(user.words))
        s = user.tags + ':'
        for j in range(50):
            s += str(vector[j]) + '\t'
        output.write(s[:-1] + '\n')
    output.close()


def getusertags():
    dict={}
    sql = "SELECT distinct(userid) FROM usersbyv where label='train'"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            dict[row[0]]=[]
    except Exception as e:
            db.rollback()
            print (e)
    sql = "SELECT userid,city FROM usersbyv where label='train'"
    try:
            cursor.execute(sql)
            results = cursor.fetchall()
            for row in results:
                userid = row[0]
                city = row[1]
                sql = "SELECT name,tags FROM flickr_china where userid='%s'and city='%s' order by takentime" % (
                userid, city)
                try:
                    cursor.execute(sql)
                    results2 = cursor.fetchall()
                    for row2 in results2:
                        dict[userid].append(row2[0])
                        dict[userid].append(row2[1])

                except Exception as e:
                    db.rollback()
                    print(e)

    except Exception as e:
            db.rollback()
            print (e)
    output = open('data/usertags.txt', 'w',encoding='utf-8')
    for user in dict:
        output.write(user + ':' + ','.join(dict[user]) + '\n')
    output.close()

def getpoitags():
    dict = {}
    sql = "SELECT poiid FROM pois_china"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            dict[row[0]] = []
    except Exception as e:
        db.rollback()
        print(e)
    sql = "SELECT poiid,name,tags FROM flickr_china where poiid is not null"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            dict[row[0]].append(row[1])
            dict[row[0]].append(row[2])
    except Exception as e:
        db.rollback()
        print(e)
    output = open('data/poitags.txt', 'w', encoding='utf-8')
    for poi in dict:
        output.write(str(poi) + ':' + ','.join(dict[poi]) + '\n')
    output.close()


# Textual Information Modeling with LDA
from gensim.parsing.preprocessing import STOPWORDS
def tokenize(text):
    text = text.lower()
    words = re.split('[, ~]', text)
    words = [w for w in words if w not in STOPWORDS]
    return words


def lda(file, num_topics):
    corpus = []  # 存储文档
    tokens = []  # 存储文档中的单词
    # 读取文档的操作
    keys = []
    for line in open('data/' + file + 'tags.txt', encoding='utf-8'):
        corpus.append(line.strip().split(':')[1])
        keys.append(line.strip().split(':')[0])
    # print(corpus)
    for text in corpus:
        tokens.append(tokenize(text))
    # print(tokens)

    dictionary = corpora.Dictionary(tokens)
    texts = [dictionary.doc2bow(text) for text in tokens]
    texts_tf_idf = models.TfidfModel(texts)[texts]
    print("**************LDA*************")
    lda = models.ldamodel.LdaModel(corpus=texts, id2word=dictionary, num_topics=num_topics, update_every=0, passes=5)
    texts_lda = lda[texts_tf_idf]
    output = open('data/' + file + 'topics_' + str(num_topics) + '.txt', 'w')
    count = 0

    for doc in texts_lda:
        s = keys[count] + ':'
        count += 1
        vector = ['0' for i in range(num_topics)]
        for topic in doc:
            vector[topic[0]] = str(topic[1])
        # print(topic)
        s += '\t'.join(vector)
        output.write(s + '\n')
    output.close()


# Obtain training, validation, and test data, and store them into matrices
import numpy as np
def getmatrix():
    users = []
    sql = "SELECT distinct(userid) FROM usersbyv"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            users.append(row[0])
    except Exception as e:
        db.rollback()
        print(e)

    pois=[]
    sql = "SELECT distinct(poiid) FROM pois_china"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            pois.append(row[0])
    except Exception as e:
        db.rollback()
        print(e)

    train=np.zeros((len(users),len(pois)))
    valid = np.zeros((len(users), len(pois)))
    test = np.zeros((len(users), len(pois)))

    sql = "SELECT userid,city FROM usersbyv where label='train'"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            userid = row[0]
            city = row[1]
            sql = "SELECT poiid,count(*) FROM visit_china where userid='%s'and city='%s' group by poiid" % (
                userid, city)
            try:
                cursor.execute(sql)
                results2 = cursor.fetchall()
                for row2 in results2:
                    poiid=row2[0]
                    vno=row2[1]
                    train[users.index(userid)][pois.index(poiid)]=vno
                    valid[users.index(userid)][pois.index(poiid)] = vno
                    test[users.index(userid)][pois.index(poiid)] = vno

            except Exception as e:
                db.rollback()
                print(e)

    except Exception as e:
        db.rollback()
        print(e)

    sql = "SELECT userid,city FROM usersbyv where label='valid'"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            userid = row[0]
            city = row[1]
            sql = "SELECT poiid,count(*) FROM visit_china where userid='%s'and city='%s' group by poiid" % (
                userid, city)
            try:
                cursor.execute(sql)
                results2 = cursor.fetchall()
                for row2 in results2:
                    poiid = row2[0]
                    vno = row2[1]
                    valid[users.index(userid)][pois.index(poiid)] = vno

            except Exception as e:
                db.rollback()
                print(e)

    except Exception as e:
        db.rollback()
        print(e)

    sql = "SELECT userid,city FROM usersbyv where label='test'"
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            userid = row[0]
            city = row[1]
            sql = "SELECT poiid,count(*) FROM visit_china where userid='%s'and city='%s' group by poiid" % (
                userid, city)
            try:
                cursor.execute(sql)
                results2 = cursor.fetchall()
                for row2 in results2:
                    poiid = row2[0]
                    vno = row2[1]
                    test[users.index(userid)][pois.index(poiid)] = vno

            except Exception as e:
                db.rollback()
                print(e)

    except Exception as e:
        db.rollback()
        print(e)
    np.savetxt('data/trainm.txt',train)
    np.savetxt('data/validm.txt',valid)
    np.savetxt('data/testm.txt',test)

# calculate cosine similarities
def calcos(file):
    vector =[]
    for line in open(file):
        vector.append(line.strip().split(':')[1].split('\t'))
        vector = np.array(vector)
        vector = vector.astype('float')
    sims=[[0 for i in range(len(vector)) ] for j in range(len(vector))]
    for i in range(len(vector)):
        for j in range(len(vector)):
            x = vector[i]
            y = vector[j]
            cos=np.dot(x, y) / (np.linalg.norm(x) * np.linalg.norm(y))
            sims[i][j] = 0.5 + 0.5 * cos #归一化
    np.savetxt(file[:-4]+'sim.txt',sims)


# calculate the co-visit probabilities of locations
from scipy import log as pcov
import numpy
from scipy import log
from scipy.optimize import curve_fit

def func(x, a, b):
    y = a * (x**b)
    return y

def polyfit(sample_dis_feq_file):
    x=[]
    y=[]
    for line in open(dis_feq_file):
        tmp=line.strip().split('\t')
        for ele in tmp:
            x.append(ele.split(',')[0])
            y.append(ele.split(',')[1])
    popt, pcov = curve_fit(func, x, y)
    return popt

def covisit_prob(dis_feq_file,sample_dis_feq_file):
    x=[]
    y=[]
    for line in open(dis_feq_file):
        tmp=line.strip().split('\t')
        for ele in tmp:
            x.append(ele.split(',')[0])
            y.append(ele.split(',')[1])
    ny=func(x, polyfit(sample_dis_feq_file)[0],polyfit(sample_dis_feq_file)[1])
    ny=numpy.asarray(ny)
    ny=numpy.reshape(ny,[1514,1514])
    numpy.savetxt('data/covisit_prob.txt', np)
