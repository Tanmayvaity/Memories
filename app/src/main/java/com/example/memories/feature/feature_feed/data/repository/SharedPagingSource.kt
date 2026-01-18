package com.example.memories.feature.feature_feed.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.feature.feature_feed.domain.model.MediaObject

class SharedPagingSource(
    private val mediaManager : MediaManager
): PagingSource<Int, MediaObject>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaObject> {
        val offset = params.key ?: 0
        val result = mediaManager.fetchMediaFromShared(offset = offset)

        return try{
            LoadResult.Page(
                data = result,
                prevKey = if(offset == 0) null else offset.minus(10),
                nextKey = if(result.isEmpty()) null else offset.plus(10)
            )
        }catch (e : Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }


    }

    override fun getRefreshKey(state: PagingState<Int, MediaObject>): Int? {
        return state.anchorPosition
    }
}