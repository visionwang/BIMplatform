var myApp = angular.module("myApp", []);



myApp.controller('myAppCtrl', function ($scope, $http) {
	
	
	
	$scope.menuClick = function(param){
		$scope[param]($scope, $http);
	}
	
	
	$scope.fileCtrl = function($scope, $http){
		$http.get('./model/queryModelInfoByRid.do?rid='+string).success(function (data,status) {
	    	$scope.data = data.data;
	    }); 
		$scope.enableTag = function(item){
			$('#muiSwitch').toggleClass('checked');
			SceneJS.getScene().getNode("myEnable",function(myEnable){
				myEnable.setEnabled(!myEnable.getEnabled());
		   	});
		}
	}
	
	$scope.treeCtrl = function($scope, $http){
		$http.get('./model/queryModelProjectTree.do?rid='+string).success(function (data,status) {  
	    	$scope.treeData = data.data.treeRoots;
	    }).error(function (data,status) {  
	    
	    });  
	    
		$scope.appnedChoose = function(item){
			item.show=true;
		}
		
		
	    $scope.setOidShow = function(item){
	    	if($scope.treeClick){
	    		$scope.treeClick.checked = !$scope.treeClick.checked
	    	}
	    	$scope.treeClick = item;
	    	item.checked = !item.checked;
	    	SceneJS.getScene().getNode(item.oid + "geometry",function (material) {
	            if(hisPick.name){
	                scene.getNode(hisPick.name + "geometry", function (material) {
	                    material.setColor(hisPick.color);//之前点过的东西还原
	                });
	            }
	            hisPick = {name:item.oid,color:material.getColor()}
	            material.setColor({r: 0, g: 1, b: 0});
	            var pTableScope= $('#pTable').scope();
	            pTableScope.oid = item.oid ;
	            $.ajax({
	          	  url:'./model/queryProperty.do',
	          	  type:'GET',
	          	  data:{oid:item.oid,rid:string}
	            }).done(function(data){
	          	  pTableScope.list = data.data  
	          	  $('#pTable').scope().$apply();
	            })
	    	});
	    }
	}
	
	
	$scope.typeCtrl = function($scope, $http){
		$http.get('./model/queryBuildingCells.do?rid='+string).success(function (data,status) {  
	    	$scope.typeList = data.data;
	    }).error(function (data,status) {  
	    }); 
		$scope.typeShowTag = function(item){
			item.checked = !item.checked;
			angular.forEach(item.oids, function(data,index,array){
				scene.getNode("flags"+data,function (myEnable) {
					myEnable.setEnabled(!myEnable.getEnabled());  
				});
			});
		}
		$scope.selectedPlaneBoxList = [];
		$scope.selectedPlaneBoxEventList = [];
		$scope.typeShowOpearate = function(item){
			
			if($scope.selectedPlaneBoxList.indexOf(item.name)==-1){
					var MenuType = function () {
			            this.message = "Directional light";
			            this["alpha.a"] = 0.6;
			            this["color.r"] = Ifc.Constants.materials['Ifc'+item.name].r;
			            this["color.g"] = Ifc.Constants.materials['Ifc'+item.name].g;
			            this["color.b"] = Ifc.Constants.materials['Ifc'+item.name].b;
			            var self = this;
			            var update = function () {
			            	angular.forEach(item.oids, function(data,index,array){
			         			scene.getNode(data+'geometry',function(mt){
			         				
			         				
			         				mt.setColor({r:self["color.r"],g:self["color.g"],b:self["color.b"]});
			         				mt.setAlpha(self["alpha.a"]);
			         			})
			            	});
			                requestAnimationFrame(update);
			            };
			            update();
			        };
			        /*var $closeButton = $('.dg .close-button');
			        if($closeButton.length==1){
			        	$closeButton.css('position','relative').after($closeButton.clone().css('position','relative').addClass('cover-button').html('还原'));
			        }*/
					var menuType = new MenuType();
					$scope.selectedPlaneBoxList.push(item.name);	
					var menubox = gui.addFolder(item.name+'类');
			        menubox.add(menuType, 'alpha.a', 0.0, 1.0);
			        menubox.add(menuType, 'color.r', 0.0, 1.0);
			        menubox.add(menuType, 'color.g', 0.0, 1.0);
			        menubox.add(menuType, 'color.b', 0.0, 1.0);
			        menubox.open();
			}
			
		}
	}
	
	$scope.pTableCtrl = function($scope, $http){
		
	}
	
	$scope.floorCtrl = function($scope, $http){
		$http.get('./model/queryModelBuildingStorey.do?rid='+string).success(function (data,status) {
			$scope.floorData = data.data;
			$scope.enableTag = function(item){
				$('#muiFloorSwitch').toggleClass('checked');
				SceneJS.getScene().getNode("myEnable",function(myEnable){
					myEnable.setEnabled(!myEnable.getEnabled());
			   	});
			}
			$scope.floorClick = function(item,obj){
				item.isActive = !item.isActive;
				angular.forEach(item.oidContains, function(data,index,array){
					scene.getNode("flags"+data,function (myEnable) {
						myEnable.setEnabled(!myEnable.getEnabled());  
					});
				});
			}
	    }); 
	}
	
	
	$scope.searchCtrl = function($scope, $http){
		$scope.bimSearch = function(){
			console.log($http);
			if($.html5Validate.isAllpass($('#searchFrom'))){
				
				$http.get('./model/searchRecord.do?rid='+string+'&keyword='+$('#searchText').val()).success(function (data,status) {
					$scope.searchList = data.data;
			    }); 
			}
			$scope.searchShow = function(item){
				item.checked = !item.checked;
				SceneJS.getScene().getNode(item.oid + "geometry",function (material) {
		            if(hisPick.name){
		                scene.getNode(hisPick.name + "geometry", function (material) {
		                    material.setColor(hisPick.color);//之前点过的东西还原
		                });
		            }
		            hisPick = {name:item.oid,color:material.getColor()}
		            material.setColor({r: 0, g: 1, b: 0});
		            var pTableScope= $('#pTable').scope();
		            pTableScope.oid = item.oid ;
		            $.ajax({
		          	  url:'./model/queryProperty.do',
		          	  type:'GET',
		          	  data:{oid:item.oid,rid:string}
		            }).done(function(data){
		          	  pTableScope.list = data.data; 
		          	  $('#pTable').scope().$apply();
		            })
		    	});
				
			}
		}
	}
	
	$scope.markCtrl = function($scope, $http){
		$scope.changColor=function(){
			$('.demo').minicolors();
		}
	}
	
	
});