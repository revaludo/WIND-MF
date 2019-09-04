function[ULmin,V]=MFall(R,UserCos,LocCos,yita,d,beta,lambda,alpha)

C=1.+yita.*R;
P=zeros(size(R));
[I,J] = find(R>0);       
for ij=1:size(I)
    P(I(ij),J(ij))=1;
end

U=rand(size(R,1),d); 
L=rand(d,size(R,2));

grad=400; 
UL=cell(grad,1);
V=zeros(grad,1);
for exp=1:grad
    UL{exp}=U*L;
%         UL{exp} = UL{exp} ./ max(max(UL{exp}));
%         [row,col] =find(R ~= 0);
    R_UL=0;
%         for ij=1:size(row)
%             R_UL=R_UL+(R(row(ij),col(ij))-UL{exp,1}(row(ij),col(ij)))^2;
%         end
    for i=1:size(R,1)
        for j=1:size(R,2)
            R_UL=R_UL+C(i,j)*(P(i,j)-UL{exp,1}(i,j))^2;
        end
    end
    Uf=0;
    for i=1:size(U,1)
        for j=1:d
            Uf=Uf+U(i,j)^2;
        end
    end
    Lf=0;
    for i=1:d
        for j=1:size(L,2)
            Lf=Lf+L(i,j)^2;
        end
    end
    SimU=0;
    UiUj=0;
    for i=1:size(U,1)
        for j=1:size(U,1)
            if i~=j
                for z=1:size(U,2)
                    UiUj=UiUj+(U(i,z)-U(j,z))^2;
                end
            %SimU=SimU+User(i,j)*UiUj;
                SimU=SimU+UserCos(i,j)*UiUj;
            end
        end
    end
    SimL=0;
    LiLj=0;
    for i=1:size(L,2)
        for j=1:size(L,2)
            if i~=j
                for z=1:size(L,1)
                    LiLj=LiLj+(L(z,i)-L(z,j))^2;
                end
                %SimL=SimL+Location(i,j)*LiLj;
                SimL=SimL+LocCos(i,j)*LiLj;
            end
        end
    end
    V(exp)=R_UL/2+(Uf+Lf)*lambda/2+(SimU+SimL)*beta/2;
    
    U0=U;
    for i=1:size(U,1)
        SimU0=0;
        for j=1:size(U,1)
            if i~=j
            %SimU0=SimU0+User(i,j)*(U(i,:)-U(j,:));
                SimU0=SimU0+UserCos(i,j)*(U(i,:)-U(j,:));
            end
        end
        U0(i,:)=SimU0;
    end
    
    L0=L;
    for i=1:size(L,2)
        SimL0=0;
        for j=1:size(L,2)
            if i~=j
            %SimL0=SimL0+Location(i,j)*(L(:,i)-L(:,j));
                SimL0=SimL0+LocCos(i,j)*(L(:,i)-L(:,j));
            end
        end
        L0(:,i)=SimL0;
    end
    U=U+alpha*(C.*(P-UL{exp,1})*(L')-lambda*U-beta*U0);
    L=L+alpha*((U')*(C.*(P-UL{exp,1}))-lambda*L-beta*L0);
end

[V ind]=min(V);
ULmin=UL{ind};
