import flask, werkzeug, time, os, zipfile, zlib
import boto3, os, cv2, shutil, json
from flask import send_file, send_from_directory, safe_join, abort

app = flask.Flask(__name__)
path=os.getcwd() #파이썬 설치경로

@app.route('/Target/<userId>', methods = ['GET', 'POST'])
def target_request(userId):
    #타겟이미지 받기
    #Original 폴더 존재하는지 확인 후 사진 받기
    if(os.path.isdir(path + '/' + userId) == False):
        os.mkdir(path + '/' + userId)

    if(os.path.isdir(path + '/' + userId + '/Original') == False):
        os.mkdir(path + '/' + userId + '/Original')

    #클라이언트에서 전달받은 타겟이미지 리스트
    target_files_ids = list(flask.request.files)
    print("\nNumber of Received TargetImages : ", len(target_files_ids))

    target_image_num = 1
    #타겟이미지 저장하는 반복문
    for file_id in target_files_ids:
        print("\nSaving Image ", str(target_image_num), "/", len(target_files_ids))

        targetimagefile = flask.request.files[file_id] #타겟이미지 받아오기
        filename = werkzeug.utils.secure_filename(targetimagefile.filename) #타겟이미지 이름

        print("Image Filename : " + targetimagefile.filename)
        #timestr = time.strftime("%Y%m%d-%H%M%S")


        targetimagefile.save(path+'/' + userId + '/Original/' + targetimagefile.filename) #타겟이미지 저장
        target_image_num = target_image_num + 1

    print("\n")
    return "TargetImage(s) Uploaded Successfully. Come Back Soon."

@app.route('/Source/<userId>', methods = ['GET', 'POST'])
def source_request(userId):
    #소스이미지 받기
    #Source 폴더 존재하는지 확인 후 사진 받기
    if(os.path.isdir(path +'/'+userId) == False):
        os.mkdir(path+'/'+userId)
    if(os.path.isdir(path + '/' + userId + '/Source') == False):
        os.mkdir(path + '/' + userId + '/Source')

    #클라이언트에서 전달받은 소스이미지 리스트
    source_files_ids = list(flask.request.files)
    print("\nNumber of Received SourceImages : ", len(source_files_ids))

    source_image_num = 1

     #소스이미지 저장하는 반복문
    for file_id in source_files_ids:
        print("\nSaving Image ", str(source_image_num), "/", len(source_files_ids))


        sourceimagefile = flask.request.files[file_id] #소스이미지 받아오기
        filename = werkzeug.utils.secure_filename(sourceimagefile.filename) #소스이미지 이름
        print("Image Filename : " + sourceimagefile.filename)

        #timestr = time.strftime("%Y%m%d-%H%M%S")

        sourceimagefile.save(path+'/' + userId + '/Source/' + sourceimagefile.filename) #소스이미지 저장
        source_image_num = source_image_num + 1
    print("\n")
    return "SourceImage(s) Uploaded Successfully. Come Back Soon."


@app.route('/Info/<userId>', methods = ['GET', 'POST'])
def info_request(userId):
    #프로필 사진 받기
    #Profile 폴더 존재하는지 확인 후 사진 받기
    if(os.path.isdir(path + '/' + userId) == False):
        os.mkdir(path + '/' + userId)

    if(os.path.isdir(path + '/' + userId + '/Profile') == False):
        os.mkdir(path + '/' + userId + '/Profile')

    #클라이언트에서 전달받은 프로필이미지 리스트
    profile_files_ids = list(flask.request.files)
    print("\nNumber of Received Profile : ", len(profile_files_ids))

    profile_image_num = 1
    #프로필 이미지 저장하는 반복문
    for file_id in profile_files_ids:
        print("\nSaving Image ", str(profile_image_num), "/", len(profile_files_ids))        profileimagefile = flask.request.files[file_id] #프로필 이미지 받아오기
        filename = werkzeug.utils.secure_filename(profileimagefile.filename) #프로필 이미지 이름
        print("Profile Filename : " + profileimagefile.filename)

        profileimagefile.save(path +'/'+ userId + '/Profile/' + profileimagefile.filename) #프로필 이미지 저장
        profile_image_num = profile_image_num + 1

    print("\n")
    return "ProfileImage(s) Uploaded Successfully. Come Back Soon."


@app.route('/Code', methods = ['GET', 'POST'])
def code_requset():
    Code = request.form['Code']
    print(Code)
    userId = request.form['userId']
    print(userId)
    return "잘 받았슈"

def create_new(path, userId, name, pic=''): #새로운 폴더나 사진 파일 생성
    #초기 경로는 path = ~파이썬 위치/User_Id
    #원본 사진 폴더 ~파이썬 위치/User_Id/original
    #~파이썬 위치/User_Id/face1...n
    #~파이썬 위치/User_Id/etc
    #User_Id 폴더 안에 (n+2)개의 폴더 //n은 얼굴 개수

    if (pic == ''): #폴더 생성
        os.mkdir(path + '/' + userId + '/' + name)

    else:   #폴더에 이미지 파일 생성
        img = cv2.imread(path +'/' + userId + '/Original/' + pic, 1)
        cv2.imwrite(os.path.join(path + '/' + userId + '/' + 'etc', pic), img)
        cv2.waitKey(0)


def detect_faces(path, userId, name, photo): #TargetImage 중에서 인물 없는 사진 골라>내기
    client = boto3.client('rekognition')  #aws rekognition
    imageTarget=open(path + '/' + userId+ '/Original/' +photo, 'rb')  #사진 열기
    response = client.detect_faces(  #얼굴 감지
        Image={'Bytes': imageTarget.read()},
            Attributes=['ALL'],
            )

    #사진에 얼굴이 0개인 경우
    if (len(response['FaceDetails']) == 0):
        #etc 폴더가 없는 경우 폴더 생성 후
        if(os.path.isdir(path + '/' + userId + '/etc') == False):
            create_new(path, userId, 'etc')

        #etc 폴더에 사진 생성
        create_new(path, userId, name, photo)
        imageTarget.close()  #이미지 닫기 !필수!
        #이걸 해야 사진 삭제 가능

        #얼굴 분류할 사진을 줄이기 위해 etc 폴더에 옮긴 후 삭제
        try:
            os.remove(path+'/'+userId+'/Original/'+photo)
        except OSError as e:
            print(e)
        else:
            print("File is deleted successfully") #확인용

    return len(response['FaceDetails'])

def compare_faces(path, userId, name, sourceFile, targetFile):  #얼굴 비교
    client = boto3.client('rekognition')

    imageSource = open(path+'/'+userId+'/Source/'+sourceFile, 'rb')
    imageTarget = open(path+'/'+userId+'/Original/'+targetFile, 'rb')

    try:
        response=client.compare_faces(SimilarityThreshold=80,
                SourceImage={'Bytes': imageSource.read()},
                TargetImage={'Bytes': imageTarget.read()})
    except OSError:
        print("확인")

    imageSource.close()
    imageTarget.close()

    if(len(response['FaceMatches'])>=1): #소스이미지의 인물이 타겟이미지에 있는 경우

        img = cv2.imread(path + '/' + userId + '/Original/' + targetFile, 1)  #타겟이미지 열기

        cv2.imwrite(os.path.join(path+'/'+userId+'/'+name, targetFile), img) #타겟이>미지를 개인 폴더에 저장
        cv2.waitKey(0)

        #타겟이미지에 인물이 1명인 경우(일치>=1, 일치하지 않는 얼굴 0인 경우)
        if(len(response['UnmatchedFaces']) == 0):
            try:
                os.remove(path + '/' + userId + '/Original/' + targetFile) #원본 삭제            except OSError as e:
                print(e)
            else:
                print("File is deleted successfully") #확인용

        return len(response['FaceMatches'])

@app.route('/classification/<userId>', methods = ['GET', 'POST'])
def classification(userId):
    path = os.getcwd()  #python 설치 경로
    target_list = os.listdir(path + '/' + userId + '/Original')    #원본파일 list


    #타겟이미지에서 etc이미지 걸러내기
    for target in target_list:
        target_file = path + '/' + userId + '/Original' + '/' +target
        detect_faces(path, userId, 'etc', target)
         #detect에서 etc에 해당하는 사진들을 삭제했음.

    print("인물 외 사진 분류 완료")

    ###
    source_dir = path + '/' + userId + '/Source' #개인 사진들 경로
    source_list = os.listdir(source_dir)    #위 경로에 있는 파일 list

    del target_list[:]

    #소스 이미지가 여러 개인 경우
    for source in source_list:
        target_list = os.listdir(path + '/' + userId + '/Original')    #etc, 앞의 얼>굴 사진들이 삭제된 원본 list

        face_dir = path + '/' + userId + '/'+ os.path.splitext(source)[0]
        os.mkdir(face_dir) #골라낸 사진을 넣을 개인 폴더 생성

        source_file = source_dir + '/' + source  #소스 이미지


        for target in target_list:
            compare_faces(path, userId, os.path.splitext(source)[0], source, target)


        del target_list[:]

        print(os.path.splitext(source)[0] +' : 사진 분류 완료')

    shutil.rmtree(source_dir)
    shutil.rmtree(path + '/' + userId + '/Original')


    print("분류가 끝났습니다.")  #타겟이미지에 소스이미지의 인물이 포함된 사진의 수

    return "분류가 끝났습니다."

#사진위치 절대경로
#추후에 코드 변경할 것
app.config["CLIENT_IMAGES"] = "/home/ubuntu/pyflask/sdi1358_@naver.com/Source"

#서버에 저장되어 있는 사진 클라이언트(안드)로 보내기
@app.route('/download/<userId>/<num>')
def downloadFile(userId, num):

    list = os.listdir("/home/ubuntu/pyflask/"+userId+"/"+num)

    zipf=zipfile.ZipFile('/home/ubuntu/pyflask/'+userId+"/"+num+'.zip', 'w', zipfile.ZIP_DEFLATED)
    #os.chdir("/home/ubuntu/pyflask/sdi1358_@naver.com/Source/")
    #with zipfile.ZipFile('source.zip', 'w', zipfile.ZIP_DEFLATED) as my_zip:
    #for img in list:
    #my_zip.write(img)
    #my_zip.close()

    os.chdir("/home/ubuntu/pyflask/"+userId+"/"+num)
    for img in list:
       # with open("/home/ubuntu/pyflask/sdi1358_@naver.com/Source/"+img,"rb") as in_file:
       # compressed = zlib.compress(in_file.read(), 9)
       # with open(img, "wb") as out_file:
       # zipf.write("/home/ubuntu/pyflask/sdi1358_@naver.com/Source/"+compressed)        if img.endswith('.jpg' or '.png'):
            zipf.write(img, compress_type = zipfile.ZIP_DEFLATED)

    zipf.close()
    return send_from_directory("/home/ubuntu/pyflask/"+userId+"/", filename=num+'.zip', as_attachment=True)

app.run(host="0.0.0.0", port=5000, debug=True)
