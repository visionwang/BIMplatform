function initStart(){
	var thisTime;
	var tt = false;
	$('.aside-box li').click(function(){
		var thisUB	=	$('.aside-box li').index($(this));
		$('.aside-box li').removeClass('hover').eq(thisUB).addClass('hover');
		//if($.trim($('.nav-slide-o').eq(thisUB).html()) != ""){
			$('.nav-slide').addClass('hover');
			$('.nav-slide-o').hide();
			$('.nav-slide-o').eq(thisUB).show();
		//}
		if(!tt){
			angular.bootstrap(document,['myApp']);
			tt = true;
		}
	})
	
	function thisMouseOut(){
		$('.nav-slide').removeClass('hover');
	}
	
	$('.nav-iconback').click(function(){
		 thisMouseOut();
	});
	
	//'./project/queryProjectByRid.do?rid='+string
	$.ajax({
	   url:"./project/queryProjectByRid.do?rid="+string,
	   type:"POST",
	   dataType:"json",
	   success:function(data){
			$.ajax({
				url:"./model/queryModelInfoByPid.do?pid="+data.data.pid,
				type:"POST",
				dataType:"json",
				success:function(res){
				var luopanData = [];
				$.each(res.data,function(index,item){
					if(item.rid==string){
						luopanData.push({name:this.name,link:'http://'+location.host+location.pathname+'?rid='+item.rid,selected:true})	
					}else{
						luopanData.push({name:this.name,link:'http://'+location.host+location.pathname+'?rid='+item.rid})
					}			
				});
				console.log(luopanData);
				$('#luopanSvg').luopan({
					data:luopanData,
				});	
			}
	})
		   }
		})
	
}