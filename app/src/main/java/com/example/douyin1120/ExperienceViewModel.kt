package com.example.douyin1120

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class ExperienceViewModel : ViewModel() {

    // 使用 StateFlow 管理经验列表状态
    private val _experiences = MutableStateFlow<List<ExperienceItem>>(generateRandomExperiences())
    val experiences: StateFlow<List<ExperienceItem>> = _experiences

    // 添加刷新状态管理
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // 添加预加载数据状态管理
    private val _isPreloading = MutableStateFlow(false)
    val isPreloading: StateFlow<Boolean> = _isPreloading

    /**
     * 切换点赞状态
     */
    fun toggleLike(index: Int) {
        val currentList = _experiences.value.toMutableList()
        if (index in currentList.indices) {
            val item = currentList[index]
            currentList[index] = item.copy(
                isLiked = !item.isLiked,
                likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
            )
            _experiences.value = currentList
        }
    }

    /**
     * 模拟下拉刷新：生成新的随机数据
     */
    fun refresh() {
        _isRefreshing.value = true
        // 模拟网络延迟
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            _experiences.value = generateRandomExperiences()
            _isRefreshing.value = false
        }, 500) // 0.5秒延迟模拟网络请求
    }

    /**
     * 模拟上拉加载更多：添加新的随机数据
     */
    fun loadMore() {
        // 如果已经在加载，则不重复加载
        if (_isPreloading.value) return

        _isPreloading.value = true
        // 模拟网络延迟
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val moreItems = generateRandomExperiences().take(4)
            _experiences.value = _experiences.value + moreItems
            _isPreloading.value = false
        }, 300) // 0.3秒延迟模拟网络请求，比刷新更短
    }

    /**
     * 生成随机的经验数据
     */
    private fun generateRandomExperiences(): List<ExperienceItem> {
        val titles = listOf(
            "高效学习的5个技巧", "旅行必备清单", "如何做一杯完美咖啡", "健身入门指南",
            "极简生活实践", "手机摄影技巧", "时间管理术", "在家做面包", "烹饪新手指南",
            "阅读习惯养成", "职场沟通技巧", "个人理财基础",
            "3分钟掌握高效学习法，成绩翻倍不是梦！",
            "出发前必看！这份旅行清单让你少带10斤行李",
            "手冲咖啡小白也能秒变咖啡师，秘诀就在这杯里",
            "零基础健身计划：7天打造自律身材",
            "扔掉90%的东西后，我的生活轻松了10倍",
            "不用单反！用手机拍出杂志级大片的5个技巧",
            "每天多出2小时？这套时间管理法太狠了",
            "不用烤箱也能做！松软拉丝的面包在家搞定",
            "厨房小白逆袭指南：5道菜征服全家味蕾",
            "从翻不开一页到一年读50本，我是这样爱上阅读的",
            "说话让人舒服，是职场最被低估的能力",
            "工资5000也能存钱？新手理财3步走稳赚不赔"
        )

        val usernames = listOf(
            "小明", "小红", "阿强", "Lily", "老张", "小美", "Kevin", "Anna",
            "Alex", "Sophia", "Tom", "Emma",
            "星辰小明", "泡泡小红", "阿强本强", "Lily酱", "张哥在线", "小美同学",
            "Kevin呀", "安娜今天开心", "Alex不是鸭梨", "Sophia不说话", "Tom不吃鱼", "Emma有点甜"
        )

        return (1..8).map { id ->
            ExperienceItem(
                id = id,
                imageUrl = "https://picsum.photos/300/${Random.nextInt(300, 600)}?random=${System.currentTimeMillis() + id}",
                title = titles[Random.nextInt(titles.size)],
                avatarUrl = "https://picsum.photos/50?random=u${System.currentTimeMillis() + id}",
                username = usernames[Random.nextInt(usernames.size)],
                likeCount = Random.nextInt(50, 300),
                isLiked = false
            )
        }
    }
}
