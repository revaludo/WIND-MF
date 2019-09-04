# coding=utf-8
import pymysql
db=pymysql.connect('localhost','root','Zaoldyck','dataset',3306)
cursor=db.cursor()

sql = "select  userid,poiid,city from flickr_china_PDBSCAN  where poiid is not null group by userid,poiid"
try:
    cursor.execute(sql)
    results = cursor.fetchall()
    for row in results:
        userid = row[0]
        poiid = row[1]
        city=row[2]
        # print ("user=%s,poi=%d" % \
        #        (userid, poiid))
        sql = "select takentime from flickr_china_PDBSCAN  where  userid ='%s' and poiid='%d' order by takentime" % (userid, poiid)
        try:
            cursor.execute(sql)
            results = cursor.fetchall()
            tms=[]
            for row in results:
                takentime = row[0]
                tms.append(takentime)
                # print ("user=%s,poi=%d,takentime=%d,city=%s" % \
                #        (userid,poiid,takentime,city))

            start = 0
            for m in range(len(tms) - 1):
                if (int(tms[m + 1]) - int(tms[m])) > 6 * 3600000:
                    temptime = 0
                    for j in range(start, m + 1):
                        temptime = temptime + int(tms[j])
                    temptime = int(temptime / (m + 1 - start))
                    sql = "insert into visit_china_PDBSCAN (userid,poiid,city,visittime) values ('%s','%d','%s','%d') " % (userid,poiid,city,temptime)
                    try:
                        cursor.execute(sql)
                        db.commit()
                    except Exception as e:
                        db.rollback()
                        print(e)
                    start = m + 1
            if start > 0:
                temptime = 0
                for j in range(start, len(tms)):
                    temptime = temptime + int(tms[j])
                temptime = int(temptime / (len(tms) - start))
                sql = "insert into visit_china_PDBSCAN (userid,poiid,city,visittime) values ('%s','%d','%s','%d') " % (
                userid, poiid, city, temptime)
                try:
                    cursor.execute(sql)
                    db.commit()
                except Exception as e:
                    db.rollback()
                    print(e)
            else:
                if len(tms) > 0:
                    temptime = 0
                    for j in range(len(tms)):
                        temptime = temptime + int(tms[j])
                    temptime = int(temptime / len(tms))
                    sql = "insert into visit_china_PDBSCAN (userid,poiid,city,visittime) values ('%s','%d','%s','%d') " % (
                    userid, poiid, city, temptime)
                    try:
                        cursor.execute(sql)
                        db.commit()
                    except Exception as e:
                        db.rollback()
                        print(e)
        except Exception as e:
            print (e)

except Exception as e:
    print (e)
db.close()
