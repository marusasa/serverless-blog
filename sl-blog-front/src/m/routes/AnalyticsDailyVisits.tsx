import Chart from 'chart.js/auto'
import { useState, useEffect, useRef } from 'react';
import { todayYear, todayMonth } from '../../util/DateUtil';
import Loading from "../components/Loading";

function AnalyticsDailyVisits() {
	const [year, setYear] = useState(todayYear());
	const [month, setMonth] = useState(todayMonth());
	const chartRef = useRef(null);
	const [theChart, setTheChart] = useState(null);
	const [reload, setReload] = useState(false);
	const [loaded, setLoaded] = useState(false);
	const year1 = todayYear();
	const year2 = year1-1;
	const year3 = year2-1;
	const year4 = year3-1;
	useEffect(() => {
		const myChart = new Chart(chartRef.current.getContext("2d"), {
			type:"line",
			data: {
				labels: [],
				datasets: [{
					label: 'Actual Views',
					data: [],
					fill: false,
					borderColor: 'rgb(75, 192, 192)',
					tension: 0.1
				}, {
					label: 'Total Views',
					data: [],
					fill: false,
					borderColor: 'rgb(75, 192, 50)',
					tension: 0.1
				},]
			}
		});
		setTheChart(myChart);
	},[chartRef]);
	useEffect(() => {
		if(theChart == null){return};
		setLoaded(false);
		fetch('/mng/analytics/daily-visits/' + year + '/' + month)
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					theChart.data.labels = data.labels;
					theChart.data.datasets[0].data = data.datasets[0].data;
					theChart.data.datasets[1].data = data.datasets[1].data;
					theChart.update();
					if(data.datasets[0].data.length == 0){
						alert("No data found.");
					}
					setLoaded(true);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load data.');
			});
		
	}, [theChart,reload]);


	const handleRefresh = (e: React.FormEvent) => {
		   e.preventDefault();
		   setReload(!reload);
		};    
	
	return (
		<>
			<div className="mb-3">
				<select className="select select-bordered select-sm mr-3" value={year}
					onChange={(e) => setYear(e.target.value)} >
					<option value={year1}>{year1}</option>
					<option value={year1}>{year2}</option>
					<option value={year3}>{year3}</option>
					<option value={year4}>{year4}</option>
				</select>
				<select className="select select-bordered select-sm  mr-3" value={month}
					onChange={(e) => setMonth(e.target.value)}>
					<option value="1">01</option>
					<option value="2">02</option>
					<option value="3">03</option>
					<option value="4">04</option>
					<option value="5">05</option>
					<option value="6">06</option>
					<option value="7">07</option>
					<option value="8">08</option>
					<option value="9">09</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
				</select>
				<button className="btn btn-sm btn-accent mr-3" onClick={handleRefresh}>Refresh</button>	
			</div>
			<Loading loaded={loaded}/>
			<div className={"mb-3 " + (loaded ? 'visible' : 'invisible')}>
				<canvas ref={chartRef} className="mb-3"/>
				<p className="mb-3">Actual Views = a visitor that stayed more than 10 seconds on a post page, or more than 3 seconds on the TOP page.</p>
				<p className="mb-3">Total Views = All visitors including bots.</p>				
			</div>
		</>
	);

}

export default AnalyticsDailyVisits