
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
		fillHtml:function(obj,args){
			return (function(){
				//清空分页栏
				obj.empty();
				if(args.nowPage>1){
					obj.append('<a href="javascript:;" class="prevPage">上一页</a>');
				}else{
					obj.remove('.prevPage');
					obj.append('<span class="disabled">上一页</span>');
				}
				//中间页码
				//当前页码为初始化时隐藏的页码时，第一页样式为tcdNumber
				if(args.nowPage != 1 && args.nowPage >= 4 && args.pageCount != 4){
					obj.append('<a href="javascript:;" class="tcdNumber">'+1+'</a>');
				}
				if(args.nowPage-2 > 2 && args.nowPage <= args.pageCount && args.pageCount > 5){
					obj.append('<span>...</span>');
				}
				var start = args.nowPage -2,end = args.nowPage+2;
				if((start > 1 && args.nowPage < 4)||args.nowPage == 1){
					end++;
				}
				if(args.nowPage > args.pageCount-4 && args.nowPage >= args.pageCount){
					start--;
				}
				for (;start <= end; start++) {
					if(start <= args.pageCount && start >= 1){
						if(start != args.nowPage){
							obj.append('<a href="javascript:;" class="tcdNumber">'+ start +'</a>');
						}else{
							obj.append('<span class="nowPage">'+ start +'</span>');
						}
					}
				}
				if(args.nowPage + 2 < args.pageCount - 1 && args.nowPage >= 1 && args.pageCount > 5){
					obj.append('<span>...</span>');
				}
				if(args.nowPage != args.pageCount && args.nowPage < args.pageCount -2  && args.pageCount != 4){
					obj.append('<a href="javascript:;" class="tcdNumber">'+args.pageCount+'</a>');
				}
				//下一页
				if(args.nowPage < args.pageCount){
					obj.append('<a href="javascript:;" class="nextPage">下一页</a>');
				}else{
					obj.remove('.nextPage');
					obj.append('<span class="disabled">下一页</span>');
				}
				//总共有多少条数据
				if(args.totalCount!=undefined){
					var totalCount = "&nbsp;&nbsp;&nbsp;&nbsp;<span class='totalCount'>共有"+args.totalCount+"条数据</span>";
					obj.append(totalCount);
					//每页显示多少条数据
					var pageSizeHtml = "&nbsp;&nbsp;&nbsp;&nbsp;<span class='pageSize'>每页显示</span><span>"+args.pageSize+" 条</span>"
					obj.append(pageSizeHtml);
				}
			})();
		}	
	};
	var blindEvent = {
		blindEvent:function(obj,args){
			return (function(){
				$(obj).off('click', 'a.tcdNumber');
				obj.on("click","a.tcdNumber",function(){
					//返回页面顶端
					// html,body 都写是为了兼容浏览器
	                $('html,body').animate({
	                    scrollTop: 0
	                }, 0);
					var nowPage = parseInt($(this).text());
					fillHtml.fillHtml(obj,{"nowPage":nowPage,"pageCount":args.pageCount,"pageSize":obj.children("span.pageSize").children("input").val()});
					if(typeof(args.backFunction)=="function"){
						var pageSize = obj.children("span.pageSize").children("input").val();
						args.backFunction(nowPage,pageSize);
					}
				});
				$(obj).off('click', 'a.prevPage');
				//上一页
				obj.on("click","a.prevPage",function(){
					//返回页面顶端
					// html,body 都写是为了兼容浏览器
	                $('html,body').animate({
	                    scrollTop: 0
	                }, 0);
					var nowPage = parseInt(obj.children("span.nowPage").text());
					fillHtml.fillHtml(obj,{"nowPage":nowPage-1,"pageCount":args.pageCount,"pageSize":obj.children("span.pageSize").children("input").val()});
					if(typeof(args.backFunction)=="function"){
						var pageSize = obj.children("span.pageSize").children("input").val();
						args.backFunction(nowPage-1,pageSize);
					}
				});
				$(obj).off('click', 'a.nextPage');
				//下一页
				obj.on("click","a.nextPage",function(){
					//返回页面顶端
					// html,body 都写是为了兼容浏览器
	                $('html,body').animate({
	                    scrollTop: 0
	                }, 0);
					var nowPage = parseInt(obj.children("span.nowPage").text());
					fillHtml.fillHtml(obj,{"nowPage":nowPage+1,"pageCount":args.pageCount,"pageSize":obj.children("span.pageSize").children("input").val()});
					if(typeof(args.backFunction)=="function"){
						var pageSize = obj.children("span.pageSize").children("input").val();
						args.backFunction(nowPage+1,pageSize);
					}
				});
			})();
		}
	};
	$.fn.createPageBar = function(options){
		var args = $.extend({
			totalCount:null,
			pageCount:null,
			pageSize:null,
			nowPage:null,
			backFunction:function(){}
		},options);
		init.init(this,args);
	};
})(jQuery);