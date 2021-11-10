import boto3
import os
import cv2


def compare_faces(sourceFile, targetFile):
    
    client=boto3.client('rekognition')

    imageSource=open(sourceFile,'rb')
    imageTarget=open(targetFile,'rb')

    response=client.compare_faces(SimilarityThreshold=80,
                                SourceImage={'Bytes': imageSource.read()},
                                TargetImage={'Bytes': imageTarget.read()})
    for faceMatch in response['FaceMatches']:
        position = faceMatch['Face']['BoundingBox']
        similarity = str(faceMatch['Similarity'])
        print('The face at ' +
                str(position['Left']) + ' ' +
                str(position['Top']) +
                ' matches with ' + similarity + '% confidence')
    imageSource.close()
    imageTarget.close()
    return len(response['FaceMatches'])

def main():
    path='C:/Users/sdi13/Downloads/'  #나중에 경로 +'사용자 이름'으로 바꿀 것
                                                                                         
    target_dir = 'C:/Users/sdi13/Downloads/blackpink' #블랙핑크 사진들 경로
    target_list = os.listdir(target_dir)    #위 경로에 있는 파일 list

    source_dir = 'C:/Users/sdi13/Downloads/source' #개인 사진들 경로
    source_list = os.listdir(source_dir)    #위 경로에 있는 파일 list
    
    source_file='C:/Users/sdi13/Downloads/jennie/1.jpg'

    num = 0
    sn = 1

    #소스 이미지가 여러 개인 경우
    for source in source_list:

        source_path = path + 'face' + str(sn)
        
        os.mkdir(source_path) #골라낸 사진을 넣을 개인 폴더 생성

        source_file = path + 'source/' + source  #소스 이미지
    
        sn += 1
        
        for target in target_list:
            target_file = target_dir + '/' + target  #타겟이 되는 이미지

            face_matches = compare_faces(source_file, target_file)

            if(face_matches>=1): #소스이미지의 인물이 타겟이미지에 있는 경우
                num += 1
                img = cv2.imread(target_file, 1)  #타겟이미지 열기
                cv2.imwrite(os.path.join(source_path, target), img) #타겟이미지를 개인 폴더에 저
                cv2.waitKey(0)
                               
    print("Face matches: " + str(num))  #타겟이미지에 소스이미지의 인물이 포함된 사진의 수 

if __name__ == "__main__":
    main()
