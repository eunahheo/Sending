import boto3, os, cv2, shutil
import json


def detect_faces(path, userId, name, photo): #TargetImage 중에서 인물 없는 사진 골라내기

    client = boto3.client('rekognition')  #aws rekognition

    imageSouces=open(path+'/test/original/'+photo, 'rb')  #사진 열기

    response = client.detect_faces(  #얼굴 감지
                Image={'Bytes': imageSouces.read()},
                Attributes=['ALL'],
            )

    #사진 얼굴이 0개인 경우
    if (len(response['FaceDetails']) == 0):
        #etc 폴더가 없는 경우 폴더 생성 후
        if(os.path.isdir(path + '/' + userId + '/' + 'etc') == False): 
            create_new(path, userId, 'etc')

        #etc 폴더에 사진 생성
        create_new(path, userId, name, photo)
        
        imageSouces.close()  #이미지 닫기 !필수!

        #얼굴 분류할 사진을 줄이기 위해 etc 폴더에 옮긴 후 삭제
        try:
            os.remove(path+'/'+userId+'/original/'+photo)
        except OSError as e:
            print(e)
        else:
            print("File is deleted successfully")  #확인용
            

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
    target_dir = 'C:/Users/sdi13/Downloads' #test(id) 폴더 경로
    target_list = os.listdir(target_dir +'/test'+ '/original')    #파일 list

    
    for target in target_list:
        target_file = target_dir + '/' +target
        detect_faces(target_dir,'test', 'etc', target)


    print("~~Done")


if __name__ == "__main__":
    main()
