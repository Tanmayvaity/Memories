package com.example.memories.core.data.data_source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.Video

class RemoteImagePagingSource(
    private val remoteMediaService: RemoteMediaService
) : PagingSource<Int, Photo>() {
    private val seenIds = mutableSetOf<Long>()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: 1
        return try{
            val response = remoteMediaService.getRemoteImageMediaResponse(page = page, perPage = params.loadSize)
            val newPhotos =  response.photos.filter { seenIds.add(it.id) }
            LoadResult.Page(
                data = newPhotos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.nextPage == null) null else page + 1
            )
        }catch (e : Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}

class RemoteVideoPagingSource(
    private val remoteMediaService: RemoteMediaService
) : PagingSource<Int, Video>() {
    private val seenIds = mutableSetOf<Long>()
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        val page = params.key ?: 1
        return try{
            val response = remoteMediaService.getRemoteVideoMediaResponse(page = page, perPage = params.loadSize)
            val newPhotos =  response.videos.filter { seenIds.add(it.id) }
            LoadResult.Page(
                data = newPhotos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.nextPage == null) null else page + 1
            )
        }catch (e : Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}