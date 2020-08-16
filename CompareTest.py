import boto3

BUCKET = "s3facejm"
KEY_SOURCE = "jennie.jpg" #개인 사진
KEY_TARGET = "bp.jpg"     #비교 사진


s3 = boto3.client('s3', aws_access_key_id='' , aws_secret_access_key='')   

#aws S3 bucket을 이용하여 얼굴 비교
#사진 한 장 비교
def compare_faces(bucket, key, bucket_target, key_target, threshold=80, region="ap-northeast-2"):
	rekognition = boto3.client("rekognition", region)
	response = rekognition.compare_faces(
	    SourceImage={
			"S3Object": {
				"Bucket": bucket,
				"Name": key,
			}
		},
		TargetImage={
			"S3Object": {
				"Bucket": bucket_target,
				"Name": key_target,
			}
		},
	    SimilarityThreshold=threshold,
	)
	return response['SourceImageFace'], response['FaceMatches']


source_face, matches = compare_faces(BUCKET, KEY_SOURCE, BUCKET, KEY_TARGET)

# the main source face
print("Source Face ({Confidence}%)".format(**source_face))

# one match for each target face
for match in matches:
	print("Target Face ({Confidence}%)".format(**match['Face']))
	print("Similarity : {}%".format(match['Similarity']))

