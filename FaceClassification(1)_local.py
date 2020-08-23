import boto3, os, cv2, shutil
import json


def detect_faces(path, userId, name, photo): #TargetImage 중에서 인물 없는 사진 골라내기

    client = boto3.client('rekognition')  #aws rekognition

    imageTarget=open(path + '/' + userId+ '/original/' +photo, 'rb')  #사진 열기

    response = client.detect_faces(  #얼굴 감지
                Image={'Bytes': imageTarget.read()},
                Attributes=['ALL'],
            )

    #사진 얼굴이 0개인 경우
    if (len(response['FaceDetails']) == 0):
        #etc 폴더가 없는 경우 폴더 생성 후
        if(os.path.isdir(path + '/' + userId + '/' + 'etc') == False): 
            create_new(path, userId, 'etc')

        #etc 폴더에 사진 생성
        create_new(path, userId, name, photo)
        
        imageTarget.close()  #이미지 닫기 !필수!
                            #이걸 해야 사진 삭제 가능

        #얼굴 분류할 사진을 줄이기 위해 etc 폴더에 옮긴 후 삭제
        try:
            os.remove(path+'/'+userId+'/original/'+photo)
        except OSError as e:
            print(e)
        else:
            print("File is deleted successfully")  #확인용

    return len(response['FaceDetails'])

       
def compare_faces(path, userId, name, sourceFile, targetFile):  #얼굴 비교

    client = boto3.client('rekognition')

    imageSource = open(path+'/'+userId+'/source/'+sourceFile, 'rb')
    imageTarget = open(path+'/'+userId+'/original/'+targetFile, 'rb')

    try:
        response=client.compare_faces(SimilarityThreshold=80,
                                    SourceImage={'Bytes': imageSource.read()},
                                    TargetImage={'Bytes': imageTarget.read()})
    except OSError:
        print("확인")
        
    imageSource.close()
    imageTarget.close()
    
    if(len(response['FaceMatches'])>=1): #소스이미지의 인물이 타겟이미지에 있는 경우
        
        img = cv2.imread(path + '/' + userId + '/original/' + targetFile, 1)  #타겟이미지 열기
        cv2.imwrite(os.path.join(path+'/'+userId+'/'+name, targetFile), img) #타겟이미지를 개인 폴더에 저
        cv2.waitKey(0)

        #타겟이미지에 인물이 1명인 경우(일치>=1, 일치하지 않는 얼굴0 인 경우)
        if(len(response['UnmatchedFaces']) == 0):
            try:
                os.remove(path + '/' + userId + '/original/' + targetFile) #원본 삭제
            except OSError as e:
                print(e)
            else:
                print("File is deleted successfully")  #확인용
       
    return len(response['FaceMatches'])

def create_new(path, userId, name, pic=''): #새로운 폴더나 사진 파일 생성
    #초기 경로는 path = ~파이썬 위치/User_Id
    #원본 사진 폴더 ~파이썬 위치/User_Id/original
    #~파이썬 위치/User_Id/face1...n
    #~파이썬 위치/User_Id/etc
    #User_Id 폴더 안에 (n+2)개의 폴더 //n은 얼굴 개수
    
    if (pic == ''): #폴더 생성
        os.mkdir(path + '/' + userId + '/' + name)
        
    else:   #폴더에 이미지 파일 생성
        img = cv2.imread(path +'/' + userId + '/original/' + pic, 1)
        cv2.imwrite(os.path.join(path + '/' + userId + '/' + 'etc', pic), img)
        cv2.waitKey(0)
        
    
def main():
    userId = 'blackpink'
    path = os.getcwd()  #python 설치 경로

    target_list = os.listdir(path + '/' + userId + '/original')    #원본파일 list

    #타겟이미지에서 etc이미지 걸러내기
    for target in target_list:                  
        target_file = path + '/' + userId + '/original' + '/' +target
        detect_faces(path, userId, 'etc', target)
        #detect에서 etc에 해당하는 사진들을 삭제했음.
    print("인물 외 사진 분류 완료")
    
    ###
    source_dir = path + '/' + userId + '/source' #개인 사진들 경로
    source_list = os.listdir(source_dir)    #위 경로에 있는 파일 list

    sn = 1

    del target_list[:]
    
    #소스 이미지가 여러 개인 경우
    for source in source_list:
        target_list = os.listdir(path + '/' + userId + '/original')    #etc, 앞의 얼굴 사진들이 삭제된 원본 list

        face_dir = path + '/' + userId + '/'+ os.path.splitext(source)[0]
        os.mkdir(face_dir) #골라낸 사진을 넣을 개인 폴더 생성

        source_file = source_dir + '/' + source  #소스 이미지
    
        sn += 1
        
        for target in target_list:
            compare_faces(path, userId, os.path.splitext(source)[0], source, target)

        del target_list[:]
        print(os.path.splitext(source)[0] +'사진 분류 완료')
                                          
    print("분류가 끝났습니다.")  #타겟이미지에 소스이미지의 인물이 포함된 사진의 수 



if __name__ == "__main__":
    main()
