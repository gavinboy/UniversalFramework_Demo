package com.universal.demo.imgs;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.Toast;

import com.universal.framwork.BaseActivity;
import com.universal.framwork.annotation.InjectView;
import com.universal.framwork.bitmapfun.ImageCache.ImageCacheParams;
import com.universal.framwork.bitmapfun.ImageFetcher;
import com.universal.framwork.demo.R;

public class ImageActivity extends BaseActivity
{
  private static final String TAG = "ImageActivity";
  @InjectView(id=R.id.gridView)
  private GridView mGridView;
  private int mGridViewItemSize;
  private int mGridViewSpace;
  private ImageAdapter mAdapter;
  //Bitmapfun������ȡͼƬ�Ķ���
  private ImageFetcher mFetcher;
  
  private DisplayMetrics dm=new DisplayMetrics();
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.gridlayout);
    this.getWindowManager().getDefaultDisplay().getMetrics(dm);
    mGridViewItemSize=this.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
    mGridViewSpace=this.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
    //����ImageFetcher������Ҫ����Context��ͼƬ�ߴ�
    mFetcher=new ImageFetcher(this ,mGridViewItemSize);
    //������������Ҫ�Ĳ���
    ImageCacheParams cacheParams = new ImageCacheParams(this, "thumb1");
    cacheParams.setMemCacheSizePercent(0.25f);
    //����ImageView��Loading��������ʾ��ͼƬ
    mFetcher.setLoadingImage(R.drawable.empty_photo);
    //ΪmFetcher��ӻ��棬�����������䣬��ôͼƬʱ�޷����صģ�mFetcher�еĴ��̻������һֱû���ͷ�
    mFetcher.addImageCache(this.getSupportFragmentManager(), cacheParams);
    mAdapter=new ImageAdapter(this, mFetcher);
    mGridView.setAdapter(mAdapter);
    
    mGridView.setOnScrollListener(new OnScrollListener()
    {
      
      @Override
      public void onScrollStateChanged(AbsListView absListView, int scrollState)
      {
        if(scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
        {
          mFetcher.setPauseWork(false);
        }else
        {
          mFetcher.setPauseWork(true);
        }
      }
      
      @Override
      public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
      {
      }
    });
    
    mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
    {
      
      @Override
      public void onGlobalLayout()
      {
        final int mColNum=(int) Math.floor(mGridView.getWidth()/mGridViewItemSize);
        final int itemHeight=mGridView.getWidth()/mColNum-mGridViewSpace; 
        mAdapter.setItemHeight(itemHeight);
        mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
    });
  }
  
  @Override
  protected void onResume()
  {
    super.onResume();
    //ȡ����ǰ�˳�����
    mFetcher.setExitTasksEarly(false);
  }
  
  @Override
  protected void onPause()
  {
    super.onPause();
    //һ��Ҫ������ǰ�˳����񣬲�Ȼ���ܵ����߳��������޷��˳�
    mFetcher.setExitTasksEarly(true);
    mFetcher.flushCache();
  }
  
  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    mFetcher.closeCache();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) {
      case R.id.clear_cache:
          mFetcher.clearCache();
          Toast.makeText(this, "����������ϣ�",
                  Toast.LENGTH_SHORT).show();
          return true;
  }
    return super.onOptionsItemSelected(item);
  }
  
  

}
