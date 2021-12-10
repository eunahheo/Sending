# Sending
AWS Rekognition을 이용한 얼굴 인식 사진 분류 안드로이드 앱

### 개발배경   
친구들, 가족들과 여행을 다녀온 뒤, 각 구성원의 얼굴 별로 사진을 분류하는 일을 해본 적이 있을 것이다.   
사진이 적은 경우에는 문제가 되지 않지만 사진의 수가 많을 경우, 이 작업은 꽤나 귀찮은 일이 된다.   
다량의 사진들을 자동으로 분류해주는 기능이 있다면 사용자들의 불편함을 덜어줄 수 있을 것이다.

<hr/>

## 시연 영상

https://user-images.githubusercontent.com/63037344/145543183-e6247c1b-c5c4-44f9-a2fd-1378392ca5a4.mov

## 프로젝트 구성도

![image](https://user-images.githubusercontent.com/63037344/145544067-06283a10-8a65-4b39-93b4-a31de50a5d38.png)

## 메인 화면

![image](https://user-images.githubusercontent.com/63037344/145545720-b0edfc40-a98b-4711-99e7-90736402ee67.png)


## 주요 기능

1. 얼굴 별 사진 분류 
 - Aws Rekognition을 이용한 얼굴 비교를 통해 얼굴 별로 사진 분류
 - 분류할 사람들의 개인 사진을 업로드 후, 분류하고자 하는 사진들을 업로드한다.
 - 각 개인의 얼굴이 포함된 사진 별로 분류가 된다. (개인 사진, 본인이 포함된 단체 사진 등)

![image](https://user-images.githubusercontent.com/63037344/145545197-51ff088d-2000-44d3-961f-441087040ff3.png)

2. 모자이크 
 - 얼굴 인식을 이용한 얼굴 인식 모자이크 기능
 - 모자이크 하고자 하는 사진을 업로드하면, 사진 속 얼굴이 인식되고, 얼굴을 클릭하면 모자이크 된다.

![image](https://user-images.githubusercontent.com/63037344/145545276-0f54fc87-0486-4659-90bb-e3a15dee4244.png)


3. 사진 달력 : 달력에 그 날의 사진을 등록

![image](https://user-images.githubusercontent.com/63037344/145543904-afe31664-d8ea-4253-9dc5-96115bbb436c.png)
