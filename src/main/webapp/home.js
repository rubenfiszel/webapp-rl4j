$(document).ready(function() {
    function trainInit(train){
	var id = train.find('.id').text();
	function trainUpdate() {
	    console.log("Loop: " + id);
//	    setTimeout(trainUpdate, 2000);
	}
	trainUpdate();
	console.log("Started timeout:");
    }

    var c = $('#trainings').children('.training');
    c.each(function(i) {
	trainInit($(this));
    })
});
