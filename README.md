# tvbox-mv
mv视频搜索服务

# 请求接口

## MV视频Vod搜索接口
http://localhost:7777/mv/vod?maxCount=100&wd=五月天后来的我们

该接口只请求MV视频    

请求参数定义
~~~
wd ：搜索值，mv名称或者歌手名 都可以
ids ：vodId,同wd
maxCount：最大返回值，返回结果里面 list 最大数量。 最大值1000
~~~
返回参数结果
~~~
{
	"code": 1,
	"page": 1,
	"pagecount": 1,
	"limit": 1,
	"total": 1,
	"list": [{
		"vod_id": "五月天",
		"vod_name": "五月天",
		"vod_actor": "五月天",
		"vod_play_from": "mv",
		"vod_pic": "http://m4.auto.itc.cn/auto/content/20230611/45d65d8a001f0c5aa008030f41c98666.jpeg",
		"vod_play_url": "五月天-出头天$http://em.21dtv.com/songs/60012964.mkv#五月天-纯真$http://em.21dtv.com/songs/60013405.mkv#五月天-兄弟$http://em.21dtv.com/songs/60137298.mkv#五月天-雌雄同体$http://em.21dtv.com/songs/60013442.mkv#五月天-洗衣机$http://em.21dtv.com/songs/60070028.mkv#五月天-为什么$http://em.21dtv.com/songs/60044059.mkv#五月天-孙悟空$http://em.21dtv.com/songs/60041009.mkv#五月天-米老鼠$http://em.21dtv.com/songs/60087264.mkv#五月天-垃圾车$http://em.21dtv.com/songs/60025892.mkv#五月天-开天窗$http://em.21dtv.com/songs/60025022.mkv#五月天-知足$http://em.21dtv.com/songs/60058693.mkv"
	}]
}
~~~

## 自定义数据源-Vod搜索接口
http://localhost:7777/vod/:index?maxCount=100&wd=五月天后来的我们

### 使用方法
1. 在`jar`所在文件目录下创建`data`文件夹
2. 将自定义数据源文件放入`data`文件夹下
3. 自定义数据源名称为数据源文件名，如`dome.json`，则数据源名称为`dome`
4. 自定义数据源文件格式如下： `视频名称,视频链接`
4. 请求接口`http://localhost:7777/vod/dome?maxCount=100&wd=五月天后来的我们`

请求路径参数定义
~~~
:index ：数据源名称，如：dome，不带文件后缀
~~~

请求参数定义
~~~
wd ：搜索值，mv名称或者歌手名 都可以
ids ：vodId,同wd
maxCount：最大返回值，返回结果里面 list 最大数量。 最大值1000
~~~
返回参数结果
~~~
同上
~~~


## MV视频搜素接口
http://localhost:7777/mv/search?maxCount=100&query=五月天后来的我们

请求参数定义
~~~
query ：搜索值，mv名称或者歌手名 都可以
maxCount：最大返回值，返回结果里面 list 最大数量。 最大值1000
~~~
返回参数结果
~~~
{
  "query": "五月天后来的我们",
  "totalHits": 2511,
  "list": [
    {
      "name": "五月天-后来的我们",
      // 歌曲名
      "songName": "后来的我们",
      // 歌手名
      "songUser": "五月天",
      // mv视频url
      "url": "http://em.21dtv.com/songs/60125089.mkv",
      // 匹配分数，越高越好
      "score": 12.460263
    },
    {
      "name": "五月天-我们",
      "songName": "我们",
      "songUser": "五月天",
      "url": "http://em.21dtv.com/songs/60045770.mkv",
      "score": 8.842199
    }
  ]
}
~~~


# TvBox配置
~~~ json
{
  "sites": [
    {
      "key": "MV_vod",
      "name": "👀┃MV┃视频",
      "type": 1,
      "api": "http://你自己域名:7777/mv/vod",
      "searchable": 1,
      "quickSearch": 1,
      "filterable": 1
    }, {
      "key": "MV_vod_DOME",
      "name": "👀┃DOME┃视频",
      "type": 1,
      "api": "http://你自己域名:7777/dome",
      "searchable": 1,
      "quickSearch": 1,
      "filterable": 1
    }
  ]
~~~


## 感谢
### 直播MV来源：
小武哥 ：https://t.me/BoBaiBroo

直播文件：https://t.me/fongmi_offical/54024/244800

### 声明
仅供学习交流，严禁用于商业用途，请于24小时内删除。