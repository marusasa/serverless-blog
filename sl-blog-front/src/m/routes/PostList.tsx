import { PostType } from '../../types/MyTypes';
import PostListItem from '../components/PostListItem';
import { useEffect, useState } from 'react';
import Loading from "../components/Loading";
import {FormTitle} from "../components/FormComp";

function PostList() {

	const [articles, setArticles] = useState<PostType[]>([]);
	const [loaded, setLoaded] = useState(false);

	useEffect(() => {
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
	}, []);

	return (
		<>
			<FormTitle text="Posts"/>
			<div className="overflow-x-auto">
				<Loading loaded={loaded}/>
				<table className={"table " + (loaded ? 'visible' : 'invisible')}>
					{/* head */}
					<thead>
						<tr>
							<th>Title</th>
							<th>Status</th>
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