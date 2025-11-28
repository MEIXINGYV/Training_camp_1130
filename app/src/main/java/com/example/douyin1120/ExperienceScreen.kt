package com.example.douyin1120

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

// ===== 新增：布局模式枚举 =====
enum class LayoutMode {
    Double, Single
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceScreen(
    viewModel: ExperienceViewModel = viewModel()
) {
    val experiences by viewModel.experiences.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val gridState = rememberLazyGridState()
    val columnState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }

    // ===== 新增：布局切换状态 =====
    var layoutMode by remember { mutableStateOf(LayoutMode.Double) }

    // 滚动到底部加载更多（双列）
    LaunchedEffect(gridState, layoutMode) {
        if (layoutMode == LayoutMode.Double) {
            snapshotFlow { gridState.layoutInfo }
                .collect { layoutInfo ->
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                    if (experiences.isNotEmpty() && lastVisibleIndex >= experiences.size - 5) {
                        viewModel.loadMore()
                    }
                }
        }
    }

    // 滚动到底部加载更多（单列）
    LaunchedEffect(columnState, layoutMode) {
        if (layoutMode == LayoutMode.Single) {
            snapshotFlow { columnState.layoutInfo }
                .collect { layoutInfo ->
                    val totalItems = layoutInfo.totalItemsCount
                    val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                    if (experiences.isNotEmpty() && lastVisible >= totalItems - 5) {
                        viewModel.loadMore()
                    }
                }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            viewModel.refresh()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        layoutMode = if (layoutMode == LayoutMode.Double) LayoutMode.Single else LayoutMode.Double
                    },
                    modifier = Modifier.padding(end = 0.dp)
                ) {
                    Text(
                        "经验",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 18.sp
                        ),
                        color = Color.Black
                    )
                }
            }

            // 搜索栏
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { query ->
                    println("搜索: $query")
                    // TODO: 实现搜索逻辑
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )

            // ===== 根据布局模式显示不同列表 =====
            when (layoutMode) {
                LayoutMode.Double -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp),
                        state = gridState,
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(experiences) { index, item ->
                            val preloadRange = 5
                            val shouldPreloadImages = index >= gridState.firstVisibleItemIndex - preloadRange &&
                                    index <= gridState.firstVisibleItemIndex +
                                    gridState.layoutInfo.visibleItemsInfo.size + preloadRange

                            ExperienceCard(
                                item = item,
                                onLikeClick = { viewModel.toggleLike(index) },
                                shouldPreloadImages = shouldPreloadImages,
                                isSingleColumn = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                LayoutMode.Single -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(8.dp),
                        state = columnState,
                        modifier = Modifier.weight(1f)
                    ) {
                        itemsIndexed(experiences) { index, item ->
                            ExperienceCard(
                                item = item,
                                onLikeClick = { viewModel.toggleLike(index) },
                                shouldPreloadImages = true,
                                isSingleColumn = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("搜索体验内容") },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        trailingIcon = {
            IconButton(onClick = { onSearch(query) }) {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            }
        }
    )
}

@Composable
fun ExperienceCard(
    item: ExperienceItem,
    onLikeClick: () -> Unit,
    shouldPreloadImages: Boolean,
    isSingleColumn: Boolean = false,
    modifier: Modifier = Modifier
) {
    // 设置宽高比：单列用 9:16 → aspectRatio = 9/16 = 0.5625f
    val aspectRatio = if (isSingleColumn) 2f else 0.75f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "体验图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isSingleColumn) 4f else 3f), // 单列更突出图片
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(if (isSingleColumn) 2f else 2f) // 可保持相同或微调
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isSingleColumn) 3 else 2, // 单列多显示一行
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = item.avatarUrl,
                        contentDescription = "用户头像",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = item.username,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (item.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (item.isLiked) "取消点赞" else "点赞",
                        tint = if (item.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onLikeClick() }
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${item.likeCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
