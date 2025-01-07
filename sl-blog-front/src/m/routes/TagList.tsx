import { useEffect, useState } from 'react';
import Loading from "../components/Loading";
import {ViewTagData} from "../../types/MyTypes";
import TagListItem from "../components/TagListItem";
import {FormTitle } from "../components/FormComp";

function TagList() {

	const [loaded, setLoaded] = useState(false);
	const [listChanged, setListChanged] = useState(false);
	const [tags, setTags] = useState<ViewTagData[]>([]);

	useEffect(() => {
		setLoaded(false);
		fetch('/mng/tags')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLoaded(true);
					setTags(data.tags);
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
		
		fetch('/mng/tags', {
			method: 'POST',
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
			});	
		
	};

	return (
		<>	
			<FormTitle text="Tags"/>
			<div className="flex">
				<button className="btn btn-primary mr-3 btn-sm" 
						onClick={handleNew}>New</button>
			</div>
			<div className="overflow-x-auto">
				<Loading loaded={loaded}/>
				<table className={"table " + (loaded ? 'visible' : 'invisible')}>
					{/* head */}
					<thead>
						<tr>
							<th>Tag</th>
							<th>ArticleIds</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						{
							tags.map((a: ViewTagData) => {
								return (<TagListItem tag={a} key={a.tagId}></TagListItem>);
							})
						}

					</tbody>
				</table>
			</div>
		</>
	)
}

export default TagList