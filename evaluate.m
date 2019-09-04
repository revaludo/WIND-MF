function[Rall,Pall,Mall,CGall]=evaluate(R,TstLoc,EmpUser,EmpLoc,topk)
Rall=zeros(size(topk,2),size(EmpUser,1));
Pall=zeros(size(topk,2),size(EmpUser,1));
Mall=zeros(size(topk,2),size(EmpUser,1));
CGall=zeros(size(topk,2),size(EmpUser,1));
Recall=zeros(size(topk));
Precision=zeros(size(topk));
for k=1:size(topk,2)
    Rec=zeros(size(EmpUser,1),topk(1,k));
    for j=1:size(EmpUser,1)
         c=TstLoc{j};
         scores=R(EmpUser(j,1),c);
         [s ind]=sort(scores,'descend');
         recindex=c(ind);
         Rec(j,:)=recindex(1:topk(1,k)); 
    end
    
     for i=1:size(EmpUser,1)
         rel=zeros(size(Rec(i,:)));
         SW=length(intersect(EmpLoc{1,i},Rec(i,:)));
         Recall(1,k)=Recall(1,k) + (SW/size(EmpLoc{1,i},2));
         Precision(1,k)=Precision(1,k) + (SW/topk(1,k));
         TP=0;
         dcg=0;
         for j=1:topk(1,k)
         	SP=length(intersect(EmpLoc{1,i},Rec(i,1:j)));
         	TP=TP+SP/j;
            if j<=length(EmpLoc{1,i})
                rel(j)=length(intersect(EmpLoc{1,i}(j),Rec(i,j)));
            end
            dcg =dcg+ ( 2^rel(j)-1) / log2(j+1);
         end
         srel=sort(rel,'descend');
         idcg=0;
         for j=1:topk(1,k)
            idcg =idcg+( 2^srel(j)-1) / log2(j+1);
         end
         if dcg==0
            ndcg=0;
         else
             ndcg=dcg/idcg;
         end
         Rall(k,i)=Rall(k,i)+SW/size(EmpLoc{1,i},2);
         Pall(k,i)= Pall(k,i)+(SW/topk(1,k));
         Mall(k,i)= Mall(k,i)+(TP/topk(1,k));
         CGall(k,i)= ndcg;
     end
end
