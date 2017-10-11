
(function($){
	var init = {
		init:function(obj,args){
			return (function(){
				fillHtml.fillHtml(obj,args);
				blindEvent.blindEvent(obj, args);
			})();
		}
	};
	var fillHtml = {
		fillHtml:function(obj,args){//.children(".col-xs-10").children(".hint-input-span-container").css('border-left-width')
			return (function(){
				  //获取后面需要多次调用的dom对象
				  var $hintSearch = obj.find(".hint-search");
				  var $hintSearchContainer = obj.find(".hint-input-span-container");
				  var $hintBlock = obj.find(".hint-block");
				  var $hintUl = obj.find(".hint-ul");
				  var $hintChose = obj.find(".hint-chose");

				  //初次调用添加词典
				  addDictionary(args.data,addUlListener);
				  //设置词典列表宽度
				  setHintSearchContainerWidth();

				  //实现响应式 监听resize事件
				  $(window).bind('resize', setHintSearchContainerWidth);

				  //获得焦点
				  $hintSearch.click(function(){
					  if($(".hint-block").is(":hidden")){
						  animteDown();
						  $hintChose.val("");
						  $(".hint-ul li").show();
					  }else{
						  animateUp();
						 
					  }
					 
				  });

				 //失去焦点
				 //设置延迟为了可以监听到click的响应
				 $hintSearch.blur(function(){
				   setTimeout(function(){
				     animateUp();
				   },200);
				 });

				 //TAB 与 enter事件
				 //监听tab与enter两个键位 如果input内有输入的内容，则添加span
				 //注意最后要阻止一下事件冒泡 防止跳转与切换焦点
				 $hintSearch.keydown(function(e){
				   switch (e.which) {
				     case 9: case 13:{

				       var text = $hintSearch.val();

				       if(!$.trim(text)) {
				         $hintSearch.val("");
				         e.preventDefault();
				         return;
				       }

				       if( !checkContainerHas(text) ) {
				         $hintSearch.before('<span class="tag label label-primary">'+ text +' <i class="fa fa-times" data-role="remove"></i><i>&nbsp;</i></span>');
				         addSpanListenr();
				       }
				       $hintSearch.val("");
				       $hintSearch.focus();
				       e.preventDefault();
				       break;
				     }
				     default: ;

				   }
				 });

				 //检测输入配对
				 //对输入内容在li中进行匹配 如果包含字符串可以找到并返回
				 //搜索方法可以自行修改，只要保证返回一个搜索后的数组即可
				 $hintSearch.keyup(function(e){

				   var text = $hintSearch.val();

				   if (!$.trim(text)){
				     updateDictionary(data.data,addUlListener);
				   }

				   var tmparr = data.data.filter(function(x){
				     return x.indexOf(text) != -1;
				   })

				   if (tmparr.length === 0) {
				     tmparr.push("无匹配条目");
				   }

				   updateDictionary(tmparr,addUlListener);
				 })



				  //函数库
				  //添加用户常用字典库
				  function addDictionary(dataarr, callback) {
					 //初始化被选中的字典
					 for(var i = 0; i < dataarr.length; i++) {
						 if(args.selectedId!=null&&args.selectedId==dataarr[i].dataTypeId){
							 $hintSearch.before('<span class="tag label label-primary"><span typeId="'+dataarr[i].dataTypeId+'">'+ dataarr[i].dataTypeName +'</span> <i class="fa fa-times remove" data-role="remove"></i></span>');
							 addSpanListenr();
						 }
					 }
				    for(var i = 0; i < dataarr.length; i++) {
				      $hintUl.append('<li typeId="'+dataarr[i].dataTypeId+'">'+ dataarr[i].dataTypeName +'</li>');
				    }
				    callback();
				  }

				  //更新搜索内容
				  function updateDictionary(dataarr,callback) {
				    $hintUl.empty();
				    addDictionary(dataarr,callback);
				  }

				  //向下滑动动画
				  //封装改变样式边框
				  function animteDown()
				  {
				    $hintBlock.slideDown('fast').css({'border':'1px solid #96C8DA','border-top' : '0px', 'box-shadow' : '0 2px 3px 0 rgba(34,36,38,.15)'});
				    $hintSearchContainer.css({'border':'1px solid #96C8DA','border-bottom' : '0px', 'box-shadow' : '0 2px 3px 0 rgba(34,36,38,.15)'});

				  }

				  //向上滑动动画
				  function animateUp()
				  {
				    $hintBlock.slideUp('fast',function(){
				      $hintSearchContainer.css({'border':'1px solid #ccc'});
				    });
				  }

				  //检验是否与输入的重复
				  function checkContainerHas(text)
				  {
				    var flag = 0;
				    $(".hint-input-span-container span.tag").each(function(){
				      if ($.trim(text) == $.trim($(this).find("span").text())) {
				         flag = 1;
				         return;
				      }
				    });
				    return flag ? true : false;
				  }
				  //设置hint-input-span-container宽度
				  function setHintSearchContainerWidth()
				  {
				    var hint_width = $hintSearchContainer.width();
				    $hintBlock.css({'width': hint_width});
				  }
				  
				  //search input事件
				  $hintChose.keyup(function(e){
					 text = $(this).val();
					 $(".hint-ul li").hide();
					 $(".hint-ul li").each(function(){
						 if(text.trim()==""){
							 $(".hint-ul li").show();
						 }else if($(this).text().indexOf(text)!=-1){
							 $(this).show();
						 }
					  });
				  });
				  
				 //绑定click事件
				 function addUlListener() {
				   $hintUl.delegate('li','click',function(){
				     var text = $(this).text();

				     if(!checkContainerHas(text)) {
				       $hintSearch.before('<span class="tag label label-primary"><span typeId="'+$(this).attr("typeid")+'">'+ text +'</span> <i class="fa fa-times remove" data-role="remove"></i></span>');
				       addSpanListenr();
				     }
				     $hintSearch.val("");
				     animateUp();
				   })
				 }

				 //监听 span事件
				 function addSpanListenr() {
				   $(".hint-input-span-container span").delegate("i",'click',function(){
				     $(this).parent().remove();
				   })
				 }
			})();
		}	
	};
	var blindEvent = {
		blindEvent:function(obj,args){
			return (function(){
			})();
		}
	};
	$.fn.createSelect = function(options){
		var args = $.extend({
			data:null,
			selectedId:null,
			backFunction:function(){}
		},options);
		var innerHtml = '<div class="col-xs-10" style="width:300px">'+
		'<div class="hint-input-span-container">'+
		'<div class="hint-search" value="">选择</div>'+
		'</div>'+
		'<div class="hint-block">'+'<input type="text" class="hint-chose" placeHolder="Search"/>'+
		'<ul class="hint-ul"></ul>'+
		'</div></div>';
		this.append(innerHtml);
		init.init(this,args);
	};
})(jQuery);