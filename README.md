#Android MVP 实践之路（一）
####一.简单介绍下MVP 
#####1.什么是mvp？

######简称：MVP 全称：Model-View-Presenter ；MVP 是从经典的模式MVC演变而来，它们的基本思想有相通的地方：Controller/Presenter负责逻辑的处理，Model提供数据，View负责显示。

#####2.mvp 与mvc 的区别？

######在MVP中View并不直接使用Model，它们之间的通信是通过Presenter (MVC中的Controller)来进行的，所有的交互都发生在Presenter内部，而在MVC中View会直接从Model中读取数据而不是通过 Controller。

#####3.优点和缺点介绍
######<font color="#000">优点：</font>
######1、模型与视图完全分离，我们可以修改视图而不影响模型
######2、可以更高效地使用模型，因为所有的交互都发生在一个地方——Presenter内部
######3、我们可以将一个Presenter用于多个视图，而不需要改变Presenter的逻辑。这个特性非常的有用，因为视图的变化总是比模型的变化频繁。
######4、如果我们把逻辑放在Presenter中，那么我们就可以脱离用户接口来测试这些逻辑（单元测试）
######<font color="#000">缺点：</font>
######由于对视图的渲染放在了Presenter中，所以视图和Presenter的交互会过于频繁。还有一点需要明白，如果Presenter过多地渲染了视图，往往会使得它与特定的视图的联系过于紧密。一旦视图需要变更，那么Presenter也需要变更了。比如说，原本用来呈现Html的Presenter现在也需要用于呈现Pdf了，那么视图很有可能也需要变更。

####二.看下实现的效果图
<img src="https://img-blog.csdn.net/20180605134104407?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x2emhvbmdkaQ==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" width=300 height=500>

####三.接下来是代码实现的过程
#####1.从创建开始
######第1步：打开Android Studio 然后新建一个工程；
######第2步：在工程中，新建三个包，分个层，分别是modle、view、presenter
######第3步：思考下我们要实现的功能，模拟网络请求，然后点击Button，实现页面上的TextView的文字改变，这中间我们用Handler模拟一下网络请求
######第4步：思考一下我们在MVC的时候，Activity要实现的方法:
	a.点击按钮请求数据
	b.当数据请求成功后，调用此接口显示数据
	c.当数据请求失败后，调用此接口提示
	d.当数据请求异常，调用此接口提示
	e.显示正在加载进度框
	f.隐藏正在加载进度框
######第5步：根据上面分析在MVC中所要实现的方法，我们来实现在MVP的View层的接口中需要实现这几个方法；
	public interface MvpView {
    /**
     * 显示正在加载进度框
     */
    void showLoading();
    /**
     * 隐藏正在加载进度框
     */
    void hideLoading();
    /**
     * 当数据请求成功后，调用此接口显示数据
     * @param data 数据源
     */
    void showData(String data);
    /**
     * 当数据请求失败后，调用此接口提示
     * @param msg 失败原因
     */
    void showFailureMessage(String msg);
    /**
     * 当数据请求异常，调用此接口提示
     */
    void showErrorMessage();
}	
######第6步：我们的View层好像写好了，接下来我们来想想我们的model层吧。其实很简单，我们说了model层是数据获取传输的层，那么在model中创建个model类，然后写一个获取数据的静态方法，这是获取到数据了，那么我们获取到的数据怎么传递给我们的Presenter呢？ 不饶了，利用接口Callback方法来进行数据的回调；两段代码如下：

MvpModel类

	public class MvpModel {
    /**
     * 获取网络接口数据
     *
     *
     * @param param    请求参数
     * @param callback 数据回调接口
     */
    public static void getNetData(final String param, final MvpCallback callback) {
        // 利用postDelayed方法模拟网络请求数据的耗时操作
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess("模拟网络请求得到的数据");
                callback.onComplete();
            }
        }, 2000);
    }
	}
MvpCallback类
	
	public interface MvpCallback {
    /**
     * 数据请求成功
     *
     * @param data 请求到的数据
     */
    void onSuccess(String data);

    /**
     * 使用网络API接口请求方式时，虽然已经请求成功但是由
     * 于{@code msg}的原因无法正常返回数据。
     */
    void onFailure(String msg);

    /**
     * 请求数据失败，指在请求网络API接口请求方式时，出现无法联网、
     * 缺少权限，内存泄露等原因导致无法连接到请求数据源。
     */
    void onError();

    /**
     * 当请求数据结束时，无论请求结果是成功，失败或是抛出异常都会执行此方法给用户做处理，通常做网络
     * 请求时可以在此处隐藏“正在加载”的等待控件
     */
    void onComplete();
    
	}
	
######第7步：如上方法实现后，那我们的View和model层都有了，我们要实现presenter层，这个是作为中间转换成，要实现model层和view层的方法；所以要传递一个实现View接口的窗口视图进来，然后调用model层的数据获取方法，将model层的数据展示给我我们的view哦；啥都不说了，代码看一眼就懂了；
MvpPresenter 类
	
	public class MvpPresenter {
    // View接口
    private MvpView mView;

    public MvpPresenter(MvpView view) {
        this.mView = view;
    }
    /**
     * 获取网络数据
     *
     * @param params 参数
     */
    public void getData(String params) {
        //显示正在加载进度条
        mView.showLoading();
        // 调用Model请求数据
        MvpModel.getNetData(params, new MvpCallback() {
            @Override
            public void onSuccess(String data) {
                //调用view接口显示数据
                mView.showData(data);
            }

            @Override
            public void onFailure(String msg) {
                //调用view接口提示失败信息
                mView.showFailureMessage(msg);
            }

            @Override
            public void onError() {
                //调用view接口提示请求异常
                mView.showErrorMessage();
            }

            @Override
            public void onComplete() {
                // 隐藏正在加载进度条
                mView.hideLoading();
            }
        });
    }
	}
######第8步：是不是很刺激？看看自己都写了什么❓，下面就是见证奇迹的时刻，我们的mainActivity中应该如何编写？布局文件很简单，就是一个线性布局，里面一个Button，一个TextView；在Activity中，要实例化Presenter类，并且那几个方法都在Presenter中实现了哦，在来一个dialog展示框就行了，对不对？上代码看看吧，会好理解一些；
MainActivity 类
	
	public class MainActivity extends AppCompatActivity implements MvpView {

    private ProgressDialog progressDialog;
    private MvpPresenter presenter;
    private TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = findViewById(R.id.textview);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在加载数据");
        presenter = new MvpPresenter(this);

    }

    /**
     * 点击事件
     * @param view
     */
    public void getDatas(View view) {
        presenter.getData("normal");
    }

    /**
     * 显示正在加载进度框
     */
    @Override
    public void showLoading() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 隐藏正在加载进度框
     */
    @Override
    public void hideLoading() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 当数据请求成功后，调用此接口显示数据
     *
     * @param data 数据源
     */
    @Override
    public void showData(String data) {
        textview.setText(data);
    }

    /**
     * 当数据请求失败后，调用此接口提示
     *
     * @param msg 失败原因
     */
    @Override
    public void showFailureMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 当数据请求异常，调用此接口提示
     */
    @Override
    public void showErrorMessage() {
        Toast.makeText(this, "网络请求数据出现异常", Toast.LENGTH_SHORT).show();
    }

	}
####四.如上就是实现的过程，这是一个简单的demo，帮助我们理解和熟悉MVP到底是什么玩意，真的想要在项目中实践，还需要花费一番功夫来弄。这个简单的demo先分享给大家，更多的MVP的内容，我会在下面的文章中更新，欢迎大家一起交流，感谢大家；

最后附上一个GitHub的下载地址
