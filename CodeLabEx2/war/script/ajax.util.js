var HOME='home';
var ENTITY_USER='user';
var ENTITY_PRODUCT='vehicle';
var ENTITY_ITEM='item';
var ENTITY_ORDER='order';
var ENTITY_ADD_POST='addPost';

var ENTITY_USER_VEHICLE_SUBSCRIPTION='userVehicleSubscription';
var ENTITY_USER_COMPANY_SUBSCRIPTION='userCompanySubscription';
var ENTITY_COMPANY_GLOBAL_SUBSCRIPTION_ID = 'companyGlobalSubscriptionId'
var ENTITY_COMPANY='company';
var ENTITY_ID = "globalSubscriptionId";
var ENTITY_VEHICLE_POST='vehiclePost';
var ENTITY_VEHICLE='vehicle';
var ENTITY_CITY='district';
var APP_ID = '161629197295331';
var APP_URL = 'http://localhost:8888/';
var userIsConnected = 0;
var name = '';
var email = '';
var userId = '';
var userKey = '';
var provider = 'facebook';
var fbImage = null;
var searchFor = "";
var userParam;
var userPosted = 0;
var currentEntity = '';
var fbName, fbCaption, fbDescription;
var dom=null;
var style;
var initDone = 0;
var cityMemoryStore = null;
var companyMemoryStore = null;
var vehicleMemoryStore = null;
var stickerMemoryStore = null;


//function to initialize the page
var init = function(dom1,style1,cityStore,companyStore,vehicleStore,stickerStore) {
	dom = dom1;
	style = style1;
	//Memory = memory1;	
	cityMemoryStore = cityStore;
	companyMemoryStore = companyStore;
	vehicleMemoryStore = vehicleStore;
	stickerMemoryStore = stickerStore;
	userParam = new Array();
	//save("blogEntry");
	fbInit();
	//showing the home tab on initializing
	showTab(HOME);
	//adding event listeners to the tabs
	$('#tabs a').click(function(event) {
		showTab(event.currentTarget.id);
	});
	
	document.getElementById('fb-logout').onclick = function() {
		fbLogout();
	};
	/*
	document.getElementById('fb-post').onclick = function() {
		fbPost();
	};
	*/	
	//$('#' + 'vehiclePost-show-ctr').hide();
	//vehiclePostStore = new dojox.data.JsonRestStore({target:"/vehiclePost"});
	//populateList(ENTITY_VEHICLE,null);
	//populateList(ENTITY_COMPANY,null);
	//populateList(ENTITY_CITY,null);
	//populateList(ENTITY_ID,null);
    $('#' + "fb-post").hide();
	$('#addPost').hide();
	style.set(dom.byId('loader'),"display","none");
}

/*
var createGrid = function () {
    companyJSONStore = new dojox.data.JsonRestStore({target:"company"});
 	companyGrid= new dojox.grid.DataGrid({
 		        store: dataStore = companyJSONStore,
 		        structure: [
 		            {name:"Company Name", field:"companyName", width: "200px"};
 		        ]
 		    }, "company-tab");
 	companyGrid.setStore(companyJSONStore);
 	companyGrid.startup();
}
*/

var fbInit = function() {
    $('#' + "fb-logout").hide();
	$('#' + "fb-login-button").show();
    $('#' + "fb-post").hide();
	
    FB.init({
        appId      : 161629197295331, // App ID
        channelUrl : 'http://localhost:8888/channel.html', // Channel File
        status     : true, // check login status
        cookie     : true, // enable cookies to allow the server to access the session
        xfbml      : true  // parse XFBML
      });
  FB.Event.subscribe('auth.login', function(response) {
	  fbUserDetail(response);
  },{scope: 'email,name,publish_actions'});

  FB.Event.subscribe('auth.logout', function(response) {
    //alert('auth.logout event handler', response);
    resetUser();
  });
  FB.Event.subscribe('auth.statusChange', function(response) {
	    //alert('auth.statusChange', response);
	fbUserDetail(response);	  
  });
  FB.Event.subscribe('auth.authResponseChange', function(response) {
	    //alert('auth.authResponseChange', response);
	  fbUserDetail(response);
  });
}

var fbUserDetail = function(response) {
	$('#' + "error-show-message").hide();
	
    if (response.authResponse) {
        FB.api('/me', function(response) {  
        	fbImage = 'https://graph.facebook.com/' + response.id + '/picture';
            name = response.name;
            email = response.email;
            userIsConnected = 1;
            userId= response.id;
            provider = "facebook";
            userParam[0]=new param("userId",userId+provider);
            if(0 ==userPosted) {
            	setUserDetails(userId,provider,name,email);
            }
            userPosted =1;
            checkUserLoggedInStatus();
    		populateList(ENTITY_USER,null);
            addUserLogin();
			showMessage("Welcome "+ name, ENTITY_USER);
           $('#' + "fb-logout").show();
   		$('#' + "fb-login-button").hide();
        });
      } else {
        //alert('User cancelled login or did not fully authorize.');
      }
}
//function to show the tab
var showTab = function(entity) {
	if((ENTITY_COMPANY == entity) || (ENTITY_ADD_POST == entity)) {
		if(0 == userIsConnected) {
			showErrMessage("Please Log in First");
			return;
		}
	}
	if((ENTITY_USER == entity) || (ENTITY_VEHICLE_POST == entity)) {
		populateMemoryStore(ENTITY_COMPANY);
	}

	if(ENTITY_ADD_POST == entity) {
		$('#home-tab').hide();
		$('#user-tab').hide();
		$('#userVehicleSubscription-tab').hide();
		$('#userCompanySubscription-tab').hide();
		$('#vehiclePost-show-ctr').hide();
		add('vehiclePost');
		populateMemoryStore(ENTITY_COMPANY_GLOBAL_SUBSCRIPTION_ID);
		return;
	}
	if(ENTITY_VEHICLE_POST == entity) {
		$('#addPost').show();
	} else {
		$('#addPost').hide();
	}
	
	$('#' + "error-show-message").hide();

	currentEntity = entity;
	//remove the active class from all the tabs
	$('.tab').removeClass("active");
	//setting the active class to the selected tab
	$('#'+entity).addClass("active");
	//hiding all the tabs
	$('.g-unit').hide();
	//showing the selected tab
	$('#' + entity + '-tab').show();
	//hiding the message block
	$('.message').hide();
	$('#' + 'vehiclePost-create-ctr').hide();
	$('#' + 'userVehicleSubscription-search-ctr').hide();
	$('#' + 'userCompanySubscription-search-ctr').hide();
	
	$('#' + 'vehiclePost-show-ctr').hide();

	//hiding the create block
	showHideCreate(entity, false);
	if(entity!=HOME)
		$('#'+entity+'-search-reset').click();
	
	if (entity == ENTITY_VEHICLE_POST){
		//style.set(dom.byId('leftCol'),"display","inline");
		//$('#' + 'vehiclePost-show-ctr').show();
	}
	else if(entity == HOME){
		$('#' + 'home-tab-left').show();
		//style.set(dom.byId('leftCol'),"display","none");
	}
}


var setUserDetails = function(userId,provider,name,email) {
	$('#' + "error-show-message").hide();

	//alert(name+provider+email+userId);
	 var data=new Array();
		// collecting the field values from the form
	 	 data[0]=new param("provider",provider);
	 	data[1]=new param("id",userId);
	 	data[2]=new param("fullName",name);
	 	data[3]=new param("eMail",email);
	 	data[4]=new param("image",fbImage);
		 //setting action as PUT
		 data[5]=new param('action','PUT');
		 //making the ajax call
		 $.ajax({
				url : "/user",
				type : "POST",
				data:data,
				success : function(data) {
				}
			});
		 //$('#'+entity+'-reset').click();
}

var resetUser = function() {
	$('#' + "error-show-message").hide();

	userIsConnected = 0;
	name = '';	
	email = '';
	userId = '';
	userKey = '';
	fbImage = '';
    userParam[0]=new param("q","none");
	populateList(ENTITY_USER,null);
	showMessage("Welcome Guest",ENTITY_USER);
	//$('#' + ENTITY_USER + '-tab').hide();
	//$('#' + ENTITY_USER_VEHICLE_SUBSCRIPTION + '-tab').hide();
	//$('#' + ENTITY_USER_COMPANY_SUBSCRIPTION + '-tab').hide();
	$('#' + "fb-login-button").show();
	$('#' + "fb-logout").hide();
	$('#' + "fb-post").hide();
}

var checkUserLoggedInStatus = function() {
	$('#' + "error-show-message").hide();

  FB.getLoginStatus(function(response) {

    if (response.status == 'connected') {
    // the user is logged in and has authenticated your
    // app, and response.authResponse supplies
    // the user's ID, a valid access token, a signed
    // request, and the time the access token 
    // and signed request each expire
    userId= response.authResponse.userID;
    userIsConnected = 1;
    var accessToken = response.authResponse.accessToken;
    $('#' + "fb-logout").show();
	$('#' + "fb-login-button").hide();

  } else if (response.status === 'not_authorized') {
	  userIsConnected = 0;
    // the user is logged in to Facebook, 
    // but has not authenticated your app
	    $('#' + "fb-logout").hide();
		$('#' + "fb-login-button").show();

  } else {
	  userIsConnected = 0;
    // the user isn't logged in to Facebook.
	    $('#' + "fb-logout").hide();
		$('#' + "fb-login-button").show();

  }    
  });
};

//document.getElementById('fb-logout').onclick = function() {
var fbLogout = function() {
	$('#' + "error-show-message").hide();

  FB.logout(function(response) {
    //alert('FB.logout callback', response);
  });
};
var fbLogin = function() {
	$('#' + "error-show-message").hide();

	  FB.login(function(response) {
	  });
	};

var fbPost = function() {
	$('#' + "error-show-message").hide();

		alert("doing post");
        // calling the API ...
        var obj = {
          method: 'feed',
          link: 'https://developers.facebook.com/docs/reference/dialogs/',
          picture: 'http://fbrell.com/f8.jpg',
          name: fbName,
          caption: fbCaption,
          description: fbDescription
        };
        function callback(response) {
          document.getElementById('user-show-message').innerHTML = "Post ID: " + response['post_id'];
        }
        FB.ui(obj, callback);
      };
      
var addUserLogin = function() {
	$('#' + "error-show-message").hide();

	//alert(userIsConnected);
	if(1 == userIsConnected) {
		$('#' + "fb-login-button").hide();
		$('#' + "fb-logout").show();
		//showMessage("Welcome "+ name + " " + email ,"user");
	}
	else {
		checkUserLoggedInStatus();
		if(0 ==userIsConnected) {
			$('#' + "fb-login-button").show();
			$('#' + "fb-logout").hide();
		}
		else {
			//showMessage("Welcome "+ name + " " + email ,"user");
		}
	}
	if(1 == userIsConnected) {
		$('#' + "fb-logout").show();
		//$('#' + "fb-post").show();
		$('#' + "fb-login-button").hide();
		//showHideCreate(ENTITY_USER, false);
	}
	else {
		$('#' + "fb-logout").hide();
		$('#' + "fb-post").hide();
		$('#' + "fb-login-button").show();
	}
}
/*
var showHideCreate = function(entity, show) {
	showMessage("Welcome "+ name, ENTITY_USER);
	if (show) {
		$('#' + entity + '-search-ctr').hide();
		$('#' + entity + '-list-ctr').hide();
		$('#' + entity + '-create-ctr').show();
	} else {
		$('#' + entity + '-search-ctr').show();
		$('#' + entity + '-list-ctr').show();
		$('#' + entity + '-create-ctr').hide();
		if(entity == ENTITY_VEHICLE_POST) {
			fillBody(0);
		}
		else if(entity!=HOME)
			populateList(entity,null);
	}
}
*/
//function to show/hide create block for an entity in a tab

var showHideCreate = function(entity, show) {
	$('#' + "error-show-message").hide();

	if(1 == userIsConnected) {
		showMessage("Welcome "+ name, ENTITY_USER);
	}
	else {
		showMessage("Welcome Guest", ENTITY_USER);

	}
	if (show) {
		if(ENTITY_VEHICLE_POST != entity) {
			$('#' + entity + '-search-ctr').hide();
			$('#' + entity + '-list-ctr').hide();
		}
		$('#' + entity + '-create-ctr').show();
	} else {
		$('#' + entity + '-search-ctr').show();
		$('#' + entity + '-list-ctr').show();
		$('#' + entity + '-create-ctr').hide();
		if(entity == ENTITY_VEHICLE_POST) {
			fillBody(0);
		}
		else if(entity!=HOME)
			populateList(entity,null);
	}
}

//parameter object definition
var param=function(name,value){
	this.name=name;
	this.value=value;
}

//function to add an entity when user clicks on the add button in UI
var add = function(entity) {
	$('#' + "error-show-message").hide();

   $('#' + 'vehiclePost-show-ctr').hide();

	if(0 == userIsConnected) {
		showErrMessage("Please Log in First");
		return;
	}
	//$('.message').hide();
	$('#'+entity+'-reset').click();
	//display the create container
	showHideCreate(entity, true);
	$("span.readonly input").attr('readonly', false);
	$("select[id$=order-user-list] > option").remove();
	$("select[id$=order-item-list] > option").remove();
	$("select[id$=item-vehicle-list] > option").remove();
	$("select[id$=state-list-vehicle-post] > option").remove();
	$("select[id$=district-list-vehicle-post] > option").remove();
	$("select[id$=company-list-vehicle-post] > option").remove();

	if(ENTITY_ADD_POST == entity){
		$('#vehiclePost-create-ctr').show();
		
	}

}

//function to search an entity when user inputs the value in the search box
var search = function(entity) {
	$('#' + "error-show-message").hide();

	//$('.message').hide();
	// collecting the field values from the form
	if(ENTITY_VEHICLE_POST == entity) {
		searchBodyReg(0);
		return;
	}
	 var formEleList = $('form#'+entity+'-search-form').serializeArray();
	 //assigning the filter criteria
	 var filterParam=new Array();
	 for(var i=0;i<formEleList.length;i++){
		 filterParam[filterParam.length]=new param(formEleList[i].name,formEleList[i].value); 
	 }
	 //calling population of the list through ajax
	populateList(entity,filterParam);
}


var showErrMessage = function(message){
		$('#'+'error-show-message').show().html('<p><b>'+message+'</b></p>');

}

var showMessage = function(message, entity){
	$('#' + "error-show-message").hide();

	if((1==userIsConnected) && (entity == ENTITY_USER)) {
		$('#'+entity+'-show-message').show().html('<p><b>'+message+'</b></p><img src='+fbImage+'></img>');
	}
	else
		$('#'+entity+'-show-message').show().html('<p><b>'+message+'</b></p>');

}

var formValidate = function(entity){
	$('#' + "error-show-message").hide();

	var key;
	var formEleList = $('form#'+entity+'-create-form').serializeArray();

	key=formEleList[0].value;
	switch(entity){
		case ENTITY_COMPANY:
			if(formEleList[0].value == ""  || formEleList[1].value == "") {
				showErrMessage('Please Fill all Mandatory Values');
				return;
			}
			break;
		case ENTITY_VEHICLE_POST:
			//var id =formEleList[0].value;
			var id = dijit.byId('uniqueId').attr('displayedValue');
			var date = dijit.byId('date').getValue();
			var errorDetails = dijit.byId('errorDetails').getValue();
			if((stickerMemoryStore.query({name:id}) == "") || (date == null) || (errorDetails == "")) {
				showErrMessage('All Values are Mandatory');
			    return;
			}
			break;
		case ENTITY_USER_VEHICLE_SUBSCRIPTION:
		case ENTITY_USER_COMPANY_SUBSCRIPTION:
			if(0==userIsConnected) {
		    	showMessage('please Login First', entity);
			}
			break;
		default :
			if(key==""){
				showMessage('please check the values in the form', entity);
				return;
			}
			break;
	}
	save(entity);
	//$('#'+entity+'-show-message').hide();
}

//function to save an entity
var save = function(entity) {
	$('#' + "error-show-message").hide();

		// creating the data object to be sent to backend
	 var data=new Array();
	// collecting the field values from the form
	 var formEleList = $('form#'+entity+'-create-form').serializeArray();
	 for(var i=0;i<formEleList.length;i++){
		data[data.length]=new param(formEleList[i].name,formEleList[i].value);
	 }
	if(entity == ENTITY_COMPANY) {
		 data[i++] = new param("userId",userId+provider);
	}
	
	 if(entity == ENTITY_USER) {
		 data[i++] = new param("id",userId);
		 data[i++] = new param ("provider",provider);
	 }
	 if((entity == ENTITY_USER_VEHICLE_SUBSCRIPTION ) || 
		 (entity == ENTITY_USER_COMPANY_SUBSCRIPTION)) {
		 data[i++] = new param("userId",userId+provider);
	 }
	 if(entity == ENTITY_VEHICLE_POST) {
		 if("true" == dijit.byId('facebook').getValue()) {
			 // POST to facebook
			 fbPost();
		 }
		 data[i++] = new param("errorDetails", dijit.byId('errorDetails').getValue());
		 data[i++] = new param("userId",userId+provider);
	 }
	 //setting action as PUT
	 data[data.length]=new param('action','PUT');
	 //making the ajax call
	 $.ajax({
			url : "/"+entity,
			type : "POST",
			data:data,
			error : function(data) {
				if(ENTITY_COMPANY == entity) {
						showErrMessage("This Company Is Already Registered By Somebody Else");
				}
			},
			success : function(data) {
				if(ENTITY_VEHICLE_POST == entity) {
	      			$('#vehiclePost-create-form').html(data);
				}
				showHideCreate(entity,false);
			}
		});
	 $('#'+entity+'-reset').click();
}

//function to edit entity
var edit = function(entity, id){
	$('#' + "error-show-message").hide();

	var parameter=new Array();
	if(ENTITY_COMPANY == entity) {
		parameter[parameter.length]=new param('companyName',id);
	} else {
		parameter[parameter.length]=new param('q',id);
	}
	$.ajax({
		url : "/"+entity,
		type : "GET",
		data:parameter,
		success : function(resp) {
			var data=resp.data[0];
			var formElements = $('form#'+entity+'-create-form :input');
			for(var i=0;i<formElements.length;i++){
				if(formElements[i].type !="button"){
					var ele=$(formElements[i]);
					if(ele.attr('name')=="vehicle"){
						$("select[id$=item-vehicle-list] > option").remove();
						ele.append('<option value="'+eval('data.'+ele.attr('name'))+'">'+eval('data.'+ele.attr('name'))+'</option>');	
					}
					else {
						ele.val(eval('data.'+ele.attr('name')));
					}
				}
			}
			showHideCreate(entity, true);
			if (entity == ENTITY_VEHICLE_POST) {
				/*
				$("select[id$=state-list-vehicle-post] > option").remove();
				$("select[id$=district-list-vehicle-post] > option").remove();
				$("select[id$=company-list-vehicle-post] > option").remove();
				populateSelectBox('state-list-vehicle-post','/state');
				populateSelectBox('district-list-vehicle-post','/district');
				populateSelectBox('company-list-vehicle-post','/company');
				*/
			}
			//$("span.readonly input").attr('readonly', true);
		}
	});
}


//function called when user clicks on the cancel button
var cancel = function(entity) {
	$('#' + "error-show-message").hide();

	//$('.message').hide();
	//hiding the create container in the tab
	showHideCreate(entity, false);
}

//function to delete an entity
var deleteEntity = function(entity,id,parentid) {
	$('#' + "error-show-message").hide();
	var parameter=new Array();
	parameter[parameter.length]=new param('name',id);
	parameter[parameter.length]=new param('parentid', parentid);
	parameter[parameter.length]=new param('action','DELETE');
	//making the ajax call
	$.ajax({
			url : "/"+entity,
			type : "POST",
			data:parameter,
			dataType:"html",
			success : function(resp) {
				showHideCreate(entity,false);
				if (resp!=''){
					showMessage(resp, entity);
				}
			},
			error : function(resp){
				showMessage(resp, entity);
			}
	});
}

// function to get the data by setting url, filter, success function and error function
var getData=function(url,filterData,successFn,errorFn){
	$('#' + "error-show-message").hide();
	
	/*
	 * if(url != "/vehiclePost" ) {
		var filterParam=new Array();
		if(1 == userIsConnected) {
			 filterParam[0]=new param("q",userId+provider);
		}
		else {
			 filterParam[0]=new param("q","none");
		}
		filterData = filterParam;
	}
	*/
	// making the ajax call
	$.ajax({
		url : url,
		type : "GET",
		data:filterData,
		success : function(resp) {
			//calling the user defined success function
			if(successFn)
			successFn(resp);	
		},
	error:function(e){
		//calling the user defined error function
		if(errorFn)
		 errorFn(e);
	}
	});
}

//function to populate the select box which takes input as id of the selectbox element and url to get the data
var populateSelectBox = function(id, url) {
	$('#' + "error-show-message").hide();

	//specifying the success function. When the ajax response is successful then the following function will be called
	var successFn=function(resp){
		//getting the select box element
		var selectBox=$('#'+id);
		//setting the content inside as empty
		selectBox.innerHTML = '';
		//getting the data from the response object
		var data=resp.data; 
		//appending the first option as select to the select box
		selectBox.append('<option value="">Select</option>');
		//adding all other values
		for (var i=0;i<data.length;i++) {
			selectBox.append('<option value="'+data[i].name+'">'+data[i].name+'</option>');
		}
	}
	//calling the getData function with the success function
	getData(url,null,successFn,null);
}

var displayBody = function(resp, success) {
	$('#' + "error-show-message").hide();

	if(1==success) {
		var data='';
		if(resp){
			data=resp.data;
		}
		//creating the html content
		if(data.length > 0){
			dom.byId("d-vehicleRegNumber").innerHTML = data[0].vehicleRegNumber;
			dom.byId("d-companyName").innerHTML = data[0].companyName;
			dom.byId("d-leasedToCompany").innerHTML = data[0].leasedToCompany;
			dom.byId("d-vehicleDetails").innerHTML = data[0].VehicleDetails;
			dom.byId("d-errorDetails").innerHTML = data[0].errorDetails;
			dom.byId("d-state").innerHTML = data[0].state;
			dom.byId("d-district").innerHTML = data[0].district;
			dom.byId("d-location").innerHTML = data[0].location;
		
			fbName = data[0].vehicleRegNumber;
			fbCaption = data[0].companyName;
			fbDescription = data[0].errorDetails;
		}
		style.set(dom.byId('vehiclePost-show-ctr'),"display","inline");

		//$('#' + 'vehiclePost-show-ctr').show();
	} else {
		var html = "<b> Sorry, We are Unable to Retrieve the data. Please Try Again </b>"
		$('#' + 'vehiclePost-show-ctr').html(html);
	}
}


var getBody=function(id){
	$('#' + "error-show-message").hide();
	$('#' + "vehiclePost-create-form").hide();
	$('#' + 'vehiclePost-show-ctr').show();

	var parameter=new Array();
	parameter[parameter.length]=new param('postId',id);
	 $.ajax({
      	  url: "/vehiclePost",
      	  type: "GET",
      	  //data: "offset="+offset,
      	   data : parameter,
      	  success: function(resp){
      		  	displayBody(resp,1);
      	  },
      	  error: function(resp){
      		  alert("error : "+resp);
      	  }
    });
}


var searchBody=function(offset,company,vehicle){
	$('#' + "error-show-message").hide();

	 var entity = ENTITY_VEHICLE_POST;
	 var commanyName = "";
	 var vehicleRegNumber = "";

	var companyName = dijit.byId('companyName2').attr('displayedValue');
	if((companyMemoryStore.query({name:companyName}) == "")) {
	    return;
	}
	 
	 var formEleList = $('form#'+entity+'-search-form').serializeArray();
	 //assigning the filter criteria
	 var filterParam=new Array();
	 for(var i=0;i<formEleList.length;i++){
		 if((formEleList[i].name == "companyName2") ) {
			 commanyName = formEleList[i].value;
		 }
		 if((formEleList[i].name == "vehicleRegNumver") ) {
			 vehicleRegNumber = formEleList[i].value;
		 }
		 filterParam[filterParam.length]=new param(formEleList[i].name,formEleList[i].value); 
	 }
	 filterParam[filterParam.length]=new param('postId',"0");
	 if(1 == company) {
		 filterParam[filterParam.length]=new param('all',"false");
		 filterParam[filterParam.length]=new param('reg',"false");
		 filterParam[filterParam.length]=new param('company',"true");
	 }
	 else {
	 	filterParam[filterParam.length]=new param('all',"false");
	 	filterParam[filterParam.length]=new param('reg',"true");
	 	filterParam[filterParam.length]=new param('company',"false");
	}
	filterParam[filterParam.length]=new param('offset',offset);
	
	 $.ajax({
      	  url: "/vehiclePost",
      	  type: "GET",
      	  //data: "offset="+offset,
      	   data : filterParam,
      	  success: function(resp){
      			$('#vehiclePost-table').html(resp);
      	  },
      	  error: function(resp){
      		  alert("error : "+resp);
      	  }
    });
}


var fillBody=function(offset){
	$('#' + "error-show-message").hide();

	var parameter=new Array();
	parameter[parameter.length]=new param('offset',offset);
	parameter[parameter.length]=new param('reg',"false");
	parameter[parameter.length]=new param('company',"false");
	parameter[parameter.length]=new param('all',"true");
	parameter[parameter.length]=new param('postId',"0");
	
	 $.ajax({
      	  url: "/vehiclePost",
      	  type: "GET",
      	  //data: "offset="+offset,
      	   data : parameter,
      	  success: function(resp){
      			$('#vehiclePost-table').html(resp);
      			if(0 == initDone) {
      				//$('#' + 'appLayout').show();

      				//style.set(dom.byId('appLayout'),"display","inline");
      				//style.set(dom.byId('before-load'),"display","none");
      				//$('#' + 'appLayout').show();
      				//$('#' + 'before-load').hide();
      			} else {
      				initDone = 1;
      			}
      	  },
      	  error: function(resp){
      		  alert("error : "+resp);
      	  }
    });
}

//function to populate the list of an entity
var populateList=function(entity, filter){
	$('#' + "error-show-message").hide();

	if((ENTITY_USER == entity) ||
			(ENTITY_USER_VEHICLE_SUBSCRIPTION == entity) ||
			(ENTITY_USER_COMPANY_SUBSCRIPTION == entity) || 
			(ENTITY_COMPANY==entity)) {
		filter = userParam;
	}
	
	//specifying the success function. When the ajax response is successful then the following function will be called
	var successFn=function(resp){
		var data='';
		if(resp){
			//alert(entity + " with response Length " + resp.data.length);
			//getting the data from the response object
			data=resp.data;
		} else {
			//alert("NO RESP FOR "+ entity);
		}
		//creating the html content
		var htm='';

		if(data.length > 0){
			for (var i=0;i<data.length;i++){
				//creating a row
				htm+='<tr>';
				switch(entity)
				{
				case ENTITY_COMPANY:
					//companyMemoryStore.add({name:data[i].companyName, id:data[i].companyName});
					htm+='<td>'+data[i].companyName+'</td><td>'+data[i].vehicleCount+'</td><td>'+data[i].stickerName+'</td>';
					break;
				case ENTITY_VEHICLE:
					vehicleMemoryStore.add({name:data[i].vehicleReg, id:data[i].vehicleReg});
					break;
				case ENTITY_ID:
					stickerMemoryStore.add({name:data[i].name, type:data[i].name});
					break;
				case ENTITY_CITY:
					cityMemoryStore.add({name:data[i].name, id:data[i].name});
					break;					
				case ENTITY_USER:
					name = data[i].fullName;
					email = data[i].eMail;
					userKey = data[i].name;
					htm+='<td>'+data[i].eMail+'</td><td>'+data[i].fullName+'</td><td>'+data[i].provider+'</td>';
					break;
				case ENTITY_USER_VEHICLE_SUBSCRIPTION:
					htm+='<td>'+data[i].vehicleRegNum+'</td><td>'+data[i].regId+'</td>';
					break;
				case ENTITY_USER_COMPANY_SUBSCRIPTION:
					htm+='<td>'+data[i].companyName+'</td>';
					break;
				case ENTITY_VEHICLE_POST:
					htm+='<td> <img src='+data[i].userImg+'></img>  Registration Number : ' +data[i].vehicleRegNumber+ '  State/City' + data[i].state+'/'+data[i].district + '</td>';					
				default:
					htm+=""; 
				}
				if(entity == ENTITY_USER) {
					if(email == "undefined") {
						htm+='<td><a href="#" class="edit-entity" onclick=\'edit("'+entity+'","'+data[i].name+'")\'>Edit</a></td></tr>';
					}
					else {
						htm+='<td>Not Applicable</td></tr>';
					}
				}
				else if(entity == ENTITY_VEHICLE_POST) {
						//htm+='<td><a href="#" class="edit-entity" onclick=\'edit("'+entity+'","'+data[i].name+'")\'>Edit</a></td></tr>';
				}
				else if(entity == ENTITY_USER_VEHICLE_SUBSCRIPTION)
					htm+='<td><a href="#" class="delete-entity" onclick=\'deleteEntity("'+entity+'","'+data[i].name+'","'+data[i].vehicleRegNum+'")\'>UnSubscribe</a></td></tr>';
				else if(entity == ENTITY_USER_COMPANY_SUBSCRIPTION)
					htm+='<td><a href="#" class="delete-entity" onclick=\'deleteEntity("'+entity+'","'+data[i].name+'","'+data[i].companyName+'")\'>UnSubscribe</a></td></tr>';
				else if(entity != ENTITY_PRODUCT)
					htm+='<td><a href="#" class="delete-entity" onclick=\'deleteEntity("'+entity+'","'+data[i].name+'",null)\'>Delete</a> | <a href="#" class="edit-entity" onclick=\'edit("'+entity+'","'+data[i].name+'")\'>Edit</a></td></tr>';
				else if(entity == ENTITY_COMPANY)
					htm+='<td><a href="#" class="delete-entity" onclick=\'deleteEntity("'+entity+'","'+data[i].name+'",null)\'>Delete</a> | <a href="#" class="edit-entity" onclick=\'edit("'+entity+'","'+data[i].name+'")\'>Edit</a></td></tr>';
				else
					htm+='<td><a href="#" class="delete-entity" onclick=\'deleteEntity("'+entity+'","'+data[i].name+'","'+data[i].userName+'")\'>Delete</a></td></tr>';
			}
		}
		else{
			//condition to show message when data is not available
			var thElesLength=$('#'+entity+'-list-ctr table thead th').length;
			htm+='<tr><td colspan="'+thElesLength+'">No Subscription Found</td></tr>';
		}
		$('#'+entity+'-list-tbody').html(htm);
	}
	getData("/"+entity,filter,successFn,null);
	if((entity == ENTITY_USER) && (currentEntity == entity)) {
		//showHideCreate(ENTITY_USER_VEHICLE_SUBSCRIPTION, false);
		showHideCreate(ENTITY_USER_COMPANY_SUBSCRIPTION, false);
		//$('#' + ENTITY_USER + '-tab').show();
		//$('#' + ENTITY_USER_VEHICLE_SUBSCRIPTION + '-tab').show();
		$('#' + ENTITY_USER_COMPANY_SUBSCRIPTION + '-tab').show();
	}
}




//function to populate the list of an entity
var populateMemoryStore=function(entity, filter){
	$('#' + "error-show-message").hide();
	//specifying the success function. When the ajax response is successful then the following function will be called
	var successFn=function(resp){
		var data='';
		if(resp){
			data=resp.data;
		}
		if(data.length > 0){
			for (var i=0;i<data.length;i++){
				//creating a row
				switch(entity)
				{
				case ENTITY_COMPANY:
					companyMemoryStore.add({name:data[i].companyName, id:data[i].companyName});
					break;
				case ENTITY_COMPANY_GLOBAL_SUBSCRIPTION_ID:
					stickerMemoryStore.add({name:data[i].name, id:data[i].name});
					break;
				default:
					break;
				}
			}
		}
	}
	getData("/"+entity,filter,successFn,null);
}