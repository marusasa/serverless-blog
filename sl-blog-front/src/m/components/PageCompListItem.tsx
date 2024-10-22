import { ViewCompData } from '../../types/MyTypes';
import { useNavigate } from "react-router-dom";

function PageCompListItem({ component }: { component: ViewCompData }) {

	const navigate = useNavigate();
	const handleEdit = (e: React.MouseEvent<HTMLButtonElement>) => {
		e.preventDefault();
		//route to edit
		navigate('/m/components/edit', { state: {pageComponentId: component.pageComponentId }});
	};
	const json = JSON.parse(component.json);
	let title = "";
	if(json.title){
		title = json.title;
	}


	return (
		<>
			<tr>
				<th>{component.type}</th>
				<td>{title}</td>
				<td>{component.order}</td>
				<td>{component.enabled?'Enabled':'Disabled'}</td>
				<td><button className="btn btn-outline btn-primary" onClick={handleEdit}>Edit</button></td>
			</tr>
		</>
	)
}

export default PageCompListItem