
function onHoraireClicked (textNode) 
{
	textNode.parentNode.className = "enCoursDEdition"; 
}

function onHoraireBlurred (textNode) {
	textNode.parentNode.className = null; 
}

/**
 * Fonction permettant d\'afficher la zone de saisie permettant de definir 
 * le nombre d\'heures et de minutes pour le décalage dans le temps
 * @param {type} param 
 */
function afficherBloqueDecalageTemps(idCourse) {
	$("decalageTemps").show();  
 	$("idCourseADecaler").value = idCourse;
} 
 
function cacherBloqueDecalageTemps() {
 	var erreur = erreurAjoutCourseAvecDecalageTemps;
 	if(erreur == false) $("decalageTemps").hide();  
}
 
 /**
  * Fonction permettant le controle de la saisie de l\'utilisateur pour les horaires : 
 */
function calculateTime(el) {
	var input = $(el);
	var output = "12:00";
	var reg=new RegExp("^[0-9]{2}[:]{1}[0-9]{2}$","g");		
	
	//La chaine est null
	if(input.value == null || input.value == "") {
		output = null;
	}
	
	//La chaine se trouve au bon format
	else if(reg.test(input.value)) {
		//Séparation de l\'heure par une expression réguliere
		var input_value = input.value.split(':');
		
		var hours = input_value.first();
		var minutes = input_value[1];
		
		hours = keepUnderValue(hours, 24, 00);
		minutes = keepUnderValue(minutes, 60, 59);
		output = hours + ':' + minutes;
	}
	
	//La chaine n\'est pas au bon format
	else {
		output = "12:00";	
	}

	input.value = output;
}

//Fonction permettant de conserver les heures inferieures à 24 et les minutes à 60
function keepUnderValue(input, under, defaultValue, dont_complete) 
{

	if (input < under) {
		out = input;
	} else {
		out = defaultValue;
	}
	out = new String(out);

	if (dont_complete == undefined) {
		if (out.length == 1) {
			out = '0'+out;
		} else if (out.length != 2) {
			out = '00';
		}
	}
	return out;
}