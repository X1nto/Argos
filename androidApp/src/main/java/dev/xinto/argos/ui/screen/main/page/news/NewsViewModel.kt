package dev.xinto.argos.ui.screen.main.page.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.xinto.argos.domain.news.NewsRepository

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    val news = newsRepository.getAllNews()
        .flow
        .cachedIn(viewModelScope)

}