import { PostType } from '../../types/MyTypes';
import PostListItem from '../components/PostListItem';
import { useEffect, useState } from 'react';
import Loading from "../components/Loading";
import {SubmitButton, FormTitle} from "../components/FormComp";
import Constants from '../../util/Constants';

function PostList() {

	const [articles, setArticles] = useState<PostType[]>([]);
	const [loaded, setLoaded] = useState(false);
	const [inProcess,setInProcess] = useState(false);
	const [listChanged, setListChanged] = useState(false);

	useEffect(() => {
		setLoaded(false);
		fetch('/mng/articles')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setLoaded(true);
					setArticles(data.articles);
				} else {
					alert(JSON.stringify(data.messages));
				}
				console.log(data);
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load articles.');
			});
	}, [listChanged]);

	const addPosts = async (title: string, body: string, status: string) => {
			await fetch('/mng/articles', {
				method: 'POST',
				body: JSON.stringify({title: title,body: body,status: status}),
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
					console.log(err.message);
				})
				.finally(() => {
					setInProcess(false);
				});
		};
	
	const handleNew = (e: React.FormEvent) => {
		e.preventDefault();
		setInProcess(true);
		addPosts("New Post", "", Constants.POST_STATUS_DRAFT);
	};
	
	return (
		<>
			<FormTitle text="Posts"/>
			<div className="flex">
				<form onSubmit={handleNew}>
					<SubmitButton text="New" inProcess={inProcess}/>
				</form>
			</div>
			<div className="overflow-x-auto">
				<Loading loaded={loaded}/>
				<table className={"table " + (loaded ? 'visible' : 'invisible')}>
					{/* head */}
					<thead>
						<tr>
							<th>Title</th>
							<th>Status</th>
							<th>Created At</th>
							<th>Published At</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						{
							articles.map((a: PostType) => {
								return (<PostListItem article={a} key={a.articleId}></PostListItem>);
							})
						}

					</tbody>
				</table>
			</div>
		</>
	)
}

export default PostList