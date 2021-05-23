package com.utopia.todolist.datasource

class DataSourceProvider {
    companion object {
        val localDataSource: IDataSource by lazy { LocalDataSourceSpImpl() }
        val remoteDataSource: IDataSource by lazy { RemoteDataSource() }
    }
}