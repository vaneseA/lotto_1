package com.example.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    //  내 번호 6개 저장
    var mMyNumList = arrayOf(3, 5, 7, 8, 9, 11)

    //    컴퓨터가 뽑은 당첨번호를 6개 저장할 ArrayList 만들어주자
    val mWinNumList = ArrayList<Int>()

    //  랜덤 번호 6개를 집어넣을 텍스트 뷰 자료형의 어레이리스트를 만들자
    val mWinNumViewList = ArrayList<TextView>()

    //    보너스 숫자 저장할 멤버 변수 생성
    var mBonusNum = 0
//    사용금액 / 당첨금액/ 당첨 횟수
    var mUsedMoney = 0
    var mEarnedMoney = 0L //30억이상의 당첨 대비, Long 타입

    var firstCount = 0
    var secondCount = 0
    var thirdCount = 0
    var fourthCount = 0
    var fifthCount = 0
    var loseCount = 0
//    핸들러로 쓰레드에 할일 할당 (postdelayed - 일정 시간 지난뒤에 할일 할당)
    lateinit var mHandler: Handler


//    핸들러가 반복 실행할 코드(로또 다시 구매)를 인터페이스 이용해 변수로 저장
    val buyLottoRunnable = object  : Runnable {
        override fun run() {
//            쓴 돈이 1000만원이 안된다면 추가 구매
            if (mUsedMoney < 10000000) {
                buyLotto()

//            핸들러에게 다음 할 일로, 이 코드를 다시 등록 (재귀함수)
                mHandler.post(this)
            }
//            천만원이 넘었다면 할일 정지
            else{

                Toast.makeText(this@MainActivity,"자동 구매 완료",Toast.LENGTH_SHORT).show()
            }
        }
    }

    var isAutoBuy = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEvents()
        setValues()
    }

    fun setupEvents() {
        buyBtn.setOnClickListener {
            buyLotto()
        }
        autoBuyBtn.setOnClickListener {
//            처음 눌리면 > 반복 구매 시작 >
//            isAutoBuy = !isAutoBuy///////////////////////////이거지우니까 돌아감
//            단순 반복 = > UI 갱신 속도가 못따라 온다.
//            while (isAutoBuy){
//                buyLotto()

//                if (mUsedMoney >= 10000000) {
//                    break
//                }
//            }
//            1회 로또 구매 명령 >완료, 다시 1회 로또 구매................연속으로 실행
            if (!isAutoBuy) {
//                핸들러에게 할일로 처음 등록
                mHandler.post(buyLottoRunnable)

                isAutoBuy = true
                autoBuyBtn.text = "자동 구매 중단"
            } else {
//                핸들러에 둥록된 다음 할 일(구매) 제거
                mHandler.removeCallbacks(buyLottoRunnable)

                isAutoBuy = false
                autoBuyBtn.text = "자동 구매 반복"
            }

        }
    }

    fun setValues() {
//      반복을 담당할 핸들러를 생성
        mHandler = Handler(Looper.getMainLooper())

        mWinNumViewList.add(winNum1Txt)
        mWinNumViewList.add(winNum2Txt)
        mWinNumViewList.add(winNum3Txt)
        mWinNumViewList.add(winNum4Txt)
        mWinNumViewList.add(winNum5Txt)
        mWinNumViewList.add(winNum6Txt)

    }

    fun buyLotto() {

        mUsedMoney += 1000
//      arrayList는 목록을 계속 누적
//      당첨 번호 새로 뽑기전에 기존의 당첨번호는 전부 랜제 하고 다시 뽑자 = 초기화
        mWinNumList.clear()
//        1.로또 당첨 번호 6개 선정
        for (i in 0 until 6) {
//          중복되지 않는 번호가 나올때 까지 반복
            while (true) {

                val randomNum = (Math.random() * 45 + 1).toInt()

//              중복 검사시 while문을 break
                if (!mWinNumList.contains(randomNum)) {
//                    당첨 번호로 뽑은 랜덤 숫자 등록
                    mWinNumList.add(randomNum)
                    break
                }

//                var isRepeat = false
//                for (winNum in mWinNumList) {
//                    if (randomNum == winNum){
//                        isRepeat = true
//                        break
//                    }
//                    mWinNumList[i] = randomNum
//                }
            }

        }
//        2. 당첨 번호 정렬 (sort) -> 작은수 ~큰 수 텍스트뷰에 표현

        mWinNumList.sort()

//        for문을 돌려서 당첨번호도 텍스트뷰에 배 /어느 텍스트 뷰인지 찾기 위해서 withIndex 라는 것을 활용
//        mWinNumList.forEachIndexed{ index, winNum ->
//            mWinNumViewList[index].text = winNum.toString()
//        }
        for ((i, winNum) in mWinNumList.withIndex()) {
            mWinNumViewList[i].text = winNum.toString()

        }
        Log.d("당첨 번호", mWinNumList.toString())
//        3. 보너스 번호 하나 선정 //텍스트뷰에 배치
        while (true) {
            val randomNum = (Math.random() * 45 + 1).toInt()

            if (!mWinNumList.contains(randomNum)) {
                mBonusNum = randomNum
                bonusNumTxt.text = mBonusNum.toString()
                break
            }
        }
        checkLottoRank()
    }

    fun checkLottoRank() {
//        4. 비교
        var correctCount = 0
//        //맞는지 테스트하는 방법2
//        val tempList = arrayListOf<Int>(3,5,7,8,9,10)
//
//        mWinNumList.clear()
//        mWinNumList.addAll(tempList)

//        //맞는지 테스트하는 방법1
//        mWinNumList.clear()
//
//        mWinNumList.add(3)
//        mWinNumList.add(5)
//        mWinNumList.add(7)
//        mWinNumList.add(8)
//        mWinNumList.add(9)
//        mWinNumList.add(10)

//      내 번호를 하나씩 조회 mMyNumlisi를 한칸씩 돌면서 myNum을 뽑아내겠
        for (myNum in mMyNumList) {
//            당첨 번호를 맞췄나?-> 당첨번호 목록에 내 번호가 들어있나?
            if (mWinNumList.contains(myNum)) {
                correctCount++
            }

        }


//        순위 선정 (텍스트 뷰 출력)

        when (correctCount) {
            6 -> {
                mEarnedMoney += 3000000000
                firstCount++
                Toast.makeText(this, "1등", Toast.LENGTH_SHORT).show()

            }
            5 -> {
//                보너스 번호를 맞췄는지?-> 보너스 번호가 내 번호 목록에 들어 있나?
                if (mMyNumList.contains(mBonusNum)) {
                    mEarnedMoney += 50000000
                    secondCount++
                    Toast.makeText(this, "임시 2등", Toast.LENGTH_SHORT).show()

                } else {
                    mEarnedMoney += 2000000
                    thirdCount ++
                    Toast.makeText(this, "임시 3등", Toast.LENGTH_SHORT).show()
                }
            }
            4 -> {
                mEarnedMoney += 50000
                fourthCount ++
                Toast.makeText(this, "4등", Toast.LENGTH_SHORT).show()
            }
            3 -> {
                mEarnedMoney += 5000
                fifthCount ++
                Toast.makeText(this, "5등", Toast.LENGTH_SHORT).show()
            }
            else -> {
                loseCount ++
                Toast.makeText(this, "낙첨", Toast.LENGTH_SHORT).show()

            }
        }
//        if(correctCount == 6) {
//
//        }else if (correctCount == 5) {
//
//        }else if (correctCount == 4) {
//
//        }else if (correctCount == 3) {
//
//        }else {//낙첨
//        }

//        사용금액/ 당첨 금액 및 횟수 텍스트에 각각 반영성
        userMoneyTxt.text = "${NumberFormat.getInstance().format(mUsedMoney)}원"
        earnMoneyTxt.text = "${NumberFormat.getInstance().format(mEarnedMoney)}원"

        winRank1Txt.text = "1등 당첨 횟수 : ${firstCount}회"
        winRank2Txt.text = "2등 당첨 횟수 : ${secondCount}회"
        winRank3Txt.text = "3등 당첨 횟수 : ${thirdCount}회"
        winRank4Txt.text = "4등 당첨 횟수 : ${fourthCount}회"
        winRank5Txt.text = "5등 당첨 횟수 : ${fifthCount}회"
        loseTxt.text = "낙첨 횟수 : ${loseCount}회"
    }

}