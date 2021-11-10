import boto3

from flask import Flask, render_template
app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

@app.route("/send")
def send():
    return render_template('/UI.html', data='HI')

@app.route('/info')
def info():
    return render_template('info.html')

@app.route('/compare') #얼굴 비교
def compare_faces():
    
    client=boto3.client('rekognition')
    FEATURES_BLACKLIST = ("Landmarks", "Emotions", "Pose", "Quality", "BoundingBox", "Confidence")

    imageSource=open('/home/eunah/pyflask/redvelvet.jpg','rb') #로컬 폴더에 있는 사진 열기
    imageTarget=open('/home/eunah/pyflask/redvelvet.jpg','rb')

    response=clie:qnt.compare_faces(SimilarityThreshold=80,
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
    
    return "Face matches: "+str(len(response['FaceMatches'])) #얼굴 매칭 수 반환