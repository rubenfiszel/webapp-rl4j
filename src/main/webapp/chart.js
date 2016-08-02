function drawChartCum(data) {
    var ctx = $("#cumReward").get(0).getContext("2d");

    var cumReward = new Chart(ctx, {
	type: 'line',
	data: {
	    labels: data[0],
	    datasets: [{
		label: 'Cumulative reward',
		data: data[1],
		backgroundColor: "rgba(72,99,160,0.2)"
	    }
		      ]
	},
	options: {
	    scales: {
		yAxes: [{
		    scaleLabel: {
			display: true,
			labelString: 'Cumulative reward'
		    },
		    display: true,
		    ticks: {
			suggestedMin: 0
		    }
		}],
		xAxes: [{
		    scaleLabel: {
			display: true,
			labelString: 'Episode'
		    },
		    ticks: {
			fontSize: 12,
			maxTicksLimit: 10,
			maxRotation: 0,
			autoSkip: true
		    },
		    display: true}]
	    }
	}
    });
};

function drawChartEps(data) {
    var ctx2 = $("#epsilon").get(0).getContext("2d");

    var epsilon = new Chart(ctx2, {
	type: 'line',
	data: {
	    labels: data[0],
	    datasets: [{
		label: 'Epsilon',
		data: data[2],
		backgroundColor: "rgba(80,90,120,0.2)"
	    }
		      ]
	},
	options: {
	    scales: {
		yAxes: [{
		    scaleLabel: {
			display: true,
			labelString: 'Epsilon'
		    },
		    display: true,
		    ticks: {
			suggestedMin: 0,
			max: 1.0
		    }
		}],
		xAxes: [{
		    scaleLabel: {
			display: true,
			labelString: 'Episode'
		    },
		    ticks: {
			fontSize: 12,
			maxTicksLimit: 10,
			maxRotation: 0,
			autoSkip: true
		    },

		    display: true}]
	    }
	}
    });

};

$(document).ready(function() {
    $.getJSON('/chart', function(data) {
	drawChartEps(data)
	drawChartCum(data)
    });
})
