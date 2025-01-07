import { ViewCompData } from '../../types/MyTypes';
import PageCompListItem from '../components/PageCompListItem';
import { useEffect, useState, useRef } from 'react';
import Loading from "../components/Loading";
import {FormTitle, SubmitButton} from "../components/FormComp";
import Constants from '../../util/Constants';

function PageCompList() {

	const [components, setComponents] = useState<ViewCompData[]>([]);
	const [loaded, setLoaded] = useState(false);
	const [newInProcess, setNewInProcess] = useState(false);
	const [newCompType, setNewCompType] = useState(Constants.VIEW_COMPONENT_TYPE_PROFILE_PIC);
	const [listChanged, setListChanged] = useState(false);
	const dialogRef = useRef(null);

	useEffect(() => {
		setLoaded(false);
		fetch('/mng/components')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLoaded(true);
					setComponents(data.components);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load data.');
			});
	}, [listChanged]);
	
	const handleNew = (e: React.FormEvent) => {
		e.preventDefault();
		setNewInProcess(true);		
		
		fetch('/mng/components', {
			method: 'POST',
			body: JSON.stringify({
				type: newCompType
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setListChanged(!listChanged);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				alert('Failed to save data.');
				console.log(err.message);
			})
			.finally(() => {
				setNewInProcess(false);
				dialogRef.current.close();
			});	
		
	};

	return (
		<>	
			<FormTitle text="Page Components"/>
			<div className="flex">
				<button className="btn btn-primary mr-3 btn-sm" 
						onClick={()=>dialogRef.current.showModal()}>New</button>
			</div>
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
			<dialog id="ask_type" className="modal" ref={dialogRef}>
				<div className="modal-box">
					<form method="dialog">
						{/* if there is a button in form, it will close the modal */}
						<button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2">✕</button>
					</form>
					<h3 className="font-bold text-lg mb-6">Select Page Component Type</h3>
					<div className="mb-6">
						<select className="select select-bordered w-full max-w-xs"								
								onChange={(e) => setNewCompType(e.target.value)}>
							<option value={Constants.VIEW_COMPONENT_TYPE_PROFILE_PIC}>Profile Picture</option>
							<option value={Constants.VIEW_COMPONENT_TYPE_LINK_LIST}>Link List</option>
							<option value={Constants.VIEW_COMPONENT_TYPE_TEXT_BOX}>Text Box</option>
							<option value={Constants.VIEW_COMPONENT_TYPE_TAGS}>Tags</option>
						</select>
					</div>
					<div className="">
						<form onSubmit={handleNew}>
							<SubmitButton text="Create" inProcess={newInProcess}/>
						</form>
					</div>
				</div>
			</dialog>
		</>
	)
}

export default PageCompList