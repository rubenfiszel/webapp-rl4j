$(document).ready(function() {
    function training(id){
	console.log(id);
    }

    var c = $('.list-group').chilren();
    $.each(c, function(i, val) {
	var id = val.child('#id').text();
	console.log(i + " " + val + " " + id);
    }
//    updateDiv();
//    setInterval(updateDiv, 5000);
});
