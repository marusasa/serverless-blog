import DataTable from 'react-data-table-component';
import { useState, useEffect } from 'react';
import { todayYear, todayMonth } from '../../util/DateUtil';
import Loading from "../components/Loading";

function AnalyticsPageEngagement() {	  
	const [year, setYear] = useState(todayYear());
	const [month, setMonth] = useState(todayMonth());
	const [reload, setReload] = useState(false);
	const [loaded, setLoaded] = useState(false);
	const year1 = todayYear();
	const year2 = year1-1;
	const year3 = year2-1;
	const year4 = year3-1;
	const [data, setData] = useState([]);
	
	useEffect(() => {
		setLoaded(false);
		fetch('/mng/analytics/page-engagement/' + year + '/' + month)
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					const newList = [];
					data.engagements.forEach(function(engagement) {
						const row = {};
						row.path = engagement.path;
						row.totalView = engagement.countTotal;
						row.actualView = engagement.countActual;
						row.average = engagement.averageTime;
						newList.push(row);
					});
					setData(newList);
					setLoaded(true);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load data.');
			});

	}, [reload]);
	
	const columns = [
		{
			name: 'Page Path',
			selector: row => row.path,
			sortable: true,
			wrap: true,
		},
		{
			name: 'Total View',
			selector: row => row.totalView,
			sortable: true,
			width: "10em",
			center: true,
		},
		{
			name: 'Actual View',
			selector: row => row.actualView,
			sortable: true,
			width: "10em",
			center: true,
		},
		{
			name: 'Average time per Actual View',
			selector: row => row.average,
			sortable: true,
			width: "10em",
			center: true,
		},
		
	];	
	
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
				<DataTable columns={columns} data={data} className="mb-3"/>
				<p className="mb-3">Actual Views = a visitor that stayed more than 10 seconds on a post page, or more than 3 seconds on the TOP page ('/').</p>
				<p className="mb-3">Total Views = All visitors including bots.</p>			
			</div>		
		</>
	);

}

export default AnalyticsPageEngagement