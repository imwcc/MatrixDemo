# MatrixDemo
自定义基于Matrix变换矩阵的可平移、缩放、旋转图片内容的view和SurefaceVeiw

TouchEvenHandler： 用于处理缩放平移旋转，并返回转换矩阵,单独抽取出来是为了避免当项目中出现大量需要自定义不同样式缩放平移控件时重复写大量代码

ZoomImageView：   可缩放平移旋转的View

SurfaceView：    可在子线程中绘制耗时内容的View，并可对绘制内容可以进行缩放平移旋转

