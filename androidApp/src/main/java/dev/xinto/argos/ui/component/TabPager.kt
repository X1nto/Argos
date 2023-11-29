package dev.xinto.argos.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.lerp

interface TabPagerScope {

    fun <T, P> tabPages(
        items: Map<T, P>,
        tabContent: @Composable (T) -> Unit,
        pageContent: @Composable (P) -> Unit
    )

    fun tabPage(
        tabContent: @Composable () -> Unit,
        pageContent: @Composable () -> Unit
    )
}

class TabPagerScopeImpl : TabPagerScope {

    private val _items: MutableMap<@Composable () -> Unit, @Composable () -> Unit> = mutableMapOf()
    val items: Map<@Composable () -> Unit, @Composable () -> Unit> = _items

    override fun <T, P> tabPages(
        items: Map<T, P>,
        tabContent: @Composable (T) -> Unit,
        pageContent: @Composable (P) -> Unit
    ) {
        items.forEach { (tab, page) ->
            this._items[{ tabContent(tab) }] = { pageContent(page) }
        }
    }

    override fun tabPage(
        tabContent: @Composable () -> Unit,
        pageContent: @Composable () -> Unit
    ) {
        _items[tabContent] = pageContent
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrimaryTabPager(
    selectedIndex: Int,
    onIndexSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: TabPagerScope.() -> Unit
) = TabPager(
    selectedIndex = selectedIndex,
    onIndexSelect = onIndexSelect,
    modifier = modifier,
    tabRow = { pager, tabs ->
        PrimaryTabRow(
            selectedTabIndex = selectedIndex,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pager, it)
                )
            },
            tabs = tabs
        )
    },
    content = content
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PrimaryScrollableTabPager(
    selectedIndex: Int,
    onIndexSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: TabPagerScope.() -> Unit
) = TabPager(
    selectedIndex = selectedIndex,
    onIndexSelect = onIndexSelect,
    modifier = modifier,
    tabRow = { pager, tabs ->
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedIndex,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pager, it)
                )
            },
            tabs = tabs
        )
    },
    content = content
)

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun SecondaryTabPager(
    selectedIndex: Int,
    onIndexSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: TabPagerScope.() -> Unit
) = TabPager(
    selectedIndex = selectedIndex,
    onIndexSelect = onIndexSelect,
    modifier = modifier,
    tabRow = { pager, tabs ->
        SecondaryTabRow(
            selectedTabIndex = selectedIndex,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pager, it)
                )
            },
            tabs = tabs
        )
    },
    content = content
)

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun SecondaryScrollableTabPager(
    selectedIndex: Int,
    onIndexSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: TabPagerScope.() -> Unit
) = TabPager(
    selectedIndex = selectedIndex,
    onIndexSelect = onIndexSelect,
    modifier = modifier,
    tabRow = { pager, tabs ->
        SecondaryScrollableTabRow(
            selectedTabIndex = selectedIndex,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pager, it)
                )
            },
            tabs = tabs
        )
    },
    content = content
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TabPager(
    selectedIndex: Int,
    onIndexSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabRow: @Composable (PagerState, @Composable () -> Unit) -> Unit,
    content: TabPagerScope.() -> Unit
) {
    val scope = remember { TabPagerScopeImpl().apply(content) }
    Column(modifier = modifier) {
        val pagerState = rememberPagerState(initialPage = 0) { scope.items.size }
        tabRow(pagerState) {
            scope.items.keys.forEachIndexed { index, text ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = {
                        onIndexSelect(index)
                    },
                    text = text
                )
            }
        }
        LaunchedEffect(selectedIndex) {
            if (pagerState.currentPage != selectedIndex) {
                pagerState.animateScrollToPage(selectedIndex)
            }
        }
        LaunchedEffect(pagerState.currentPageOffsetFraction) {
            if (pagerState.currentPageOffsetFraction == 0f) {
                onIndexSelect(pagerState.currentPage)
            }
        }
        HorizontalPager(state = pagerState) {
            scope.items.values.elementAt(it)()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffsetFraction
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.width, nextTab.width, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
        } else {
            currentTab.width.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}