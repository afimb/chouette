//define Chouette namespace
var Chouette = {};

//define sub namespaces
Chouette.Map = {};

// Initialize Cookie
Cookie.init({name: 'Chouette', expires: 90});

function showIt(idToShow, idOrigin) {
    $(idToShow).show();
    //$(idOrigin).hide();
  }
 
 function hideIt(idTohide, idOrigin) {
	    $(idTohide).hide();
	    //$(idOrigin).show();
	  }
 
 function showThem(cssClass){ 
	 for (var index = 0; index < $$('div[class='+cssClass+']').length; ++index) {
		  var item = $$('div[class='+cssClass+']')[index];
		  item.show();
		}	 
 }
 
 function hideThem(cssClass){
	 for (var index = 0; index < $$('div[class='+cssClass+']').length; ++index) {
		  var item = $$('div[class='+cssClass+']')[index];
		  item.hide();
		}		
 }
 
 function changeImage(element) { 	 
	  element.src = (element.src == "<s:url value='../images/plus.png'/>") ? 
			  "<s:url value='../images/moins.png'/>" : "<s:url value='../images/plus.png'/>"; 
	} 