import { ViewTagData } from '../../types/MyTypes';
import { useNavigate } from "react-router-dom";

function TagListItem({ tag }: { tag: ViewTagData }) {

	const navigate = useNavigate();
	const handleEdit = (e: React.MouseEvent<HTMLButtonElement>) => {
		e.preventDefault();
		//route to edit
		navigate('/m/tags/edit', { state: {tagId: tag.tagId }});
	};
	const json = JSON.parse(tag.json);


	return (
		<>
			<tr>
				<th>{tag.name}</th>
				<td>{json.map((id:string) => {return (id + ', ')})}</td>
				<td><button className="btn btn-outline btn-primary" onClick={handleEdit}>Edit</button></td>
			</tr>
		</>
	)
}

export default TagListItem