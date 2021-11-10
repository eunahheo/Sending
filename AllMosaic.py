import boto3
import io
from PIL import Image, ImageDraw, ExifTags, ImageColor, ImageFilter 

def show_faces(photo,bucket):
     

    client=boto3.client('rekognition')

    # Load image from S3 bucket
    s3_connection = boto3.resource('s3')
    s3_object = s3_connection.Object(bucket,photo)
    s3_response = s3_object.get()

    stream = io.BytesIO(s3_response['Body'].read())
    image=Image.open(stream)
    
    #Call DetectFaces 
    response = client.detect_faces(Image={'S3Object': {'Bucket': bucket, 'Name': photo}},
        Attributes=['ALL'])

    imgWidth, imgHeight = image.size  
    draw = ImageDraw.Draw(image)  
                    

    # calculate and display bounding boxes for each detected face       
    print('Detected faces for ' + photo)    
    for faceDetail in response['FaceDetails']:
        print('The detected face is between ' + str(faceDetail['AgeRange']['Low']) 
              + ' and ' + str(faceDetail['AgeRange']['High']) + ' years old')
        
        box = faceDetail['BoundingBox']
        left = imgWidth * box['Left']
        top = imgHeight * box['Top']
        width = imgWidth * box['Width']
        height = imgHeight * box['Height']
                

        print('Left: ' + '{0:.0f}'.format(left))
        print('Top: ' + '{0:.0f}'.format(top))
        print('Face Width: ' + "{0:.0f}".format(width))
        print('Face Height: ' + "{0:.0f}".format(height))

        points = (
            (left,top),
            (left + width, top),
            (left + width, top + height),
            (left , top + height),
            (left, top)

        )

        leftt = int(left)
        topp = int(top)
        widthh = leftt+int(width)
        heightt = topp+int(height)

        #모자이크 처리하기
        crop_iamge=image.crop((leftt, topp, widthh, heightt))  #(x1, y1, x2, y2) , 얼굴 위치 잘라내기
        blur_image=crop_iamge.filter(ImageFilter.GaussianBlur(radius=10)) #잘라낸 얼굴 부분 모자이크
        image.paste(blur_image, (leftt, topp)) #모자이크한 이미지 붙이기
 
    image.show()

    return len(response['FaceDetails'])

def main():
    bucket="s3facejm"
    photo="ss.jpg"

    faces_count=show_faces(photo,bucket)
    print("faces detected: " + str(faces_count))


if __name__ == "__main__":
    main()
