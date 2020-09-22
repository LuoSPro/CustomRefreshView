package com.example.refreshview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.refreshview.refreshlistview.RefreshViewAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter: RefreshViewAdapter
                = RefreshViewAdapter(
            this, 0, listOf(
                "第1个", "第2个", "第3个", "第4个",
                "第5个", "第6个", "第7个", "第8个", "第9个", "第10个", "第11个", "第12个", "第13个", "第14个"
            )
        )
        lv_refresh_list.apply {
            setOnMyListViewListener {
                handler.postDelayed(
                    {
                        lv_refresh_list.setRefreshingFinish()
                        lv_refresh_list.setRefreshingTime()
                    },2000
                )
            }
            setAdapter(adapter)
        }
    }
}
