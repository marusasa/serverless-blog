import { ViewCompData } from '../../types/MyTypes';
import PageCompListItem from '../components/PageCompListItem';
import { useEffect, useState } from 'react';
import Loading from "../components/Loading";
import {FormTitle} from "../components/FormComp";

function PageCompList() {

	const [components, setComponents] = useState<ViewCompData[]>([]);
	const [loaded, setLoaded] = useState(false);

	useEffect(() => {
		fetch('/mng/components')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLoaded(true);
					setComponents(data.components);
				} else {
					alert(JSON.stringify(data.messages));
				}
				console.log(data);
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load data.');
			});
	}, []);

	return (
		<>	
			<FormTitle text="Page Components"/>
			<div className="overflow-x-auto">
				<Loading loaded={loaded}/>
				<table className={"table " + (loaded ? 'visible' : 'invisible')}>
					{/* head */}
					<thead>
						<tr>
							<th>Type</th>
							<th>Title</th>
							<th>Order</th>
							<th>Enabled</th>
						</tr>
					</thead>
					<tbody>
						{
							components.map((a: ViewCompData) => {
								return (<PageCompListItem component={a} key={a.pageComponentId}></PageCompListItem>);
							})
						}

					</tbody>
				</table>
			</div>
		</>
	)
}

export default PageCompList