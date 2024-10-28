import { PostType } from '../../types/MyTypes';
import { useNavigate } from "react-router-dom";

function PostListItem({ article }: { article: PostType }) {

	const navigate = useNavigate();
	//formate datetime.
	//createdAt & publishedAt will be in UTC timezone with
	//format: '2024-10-25T04:36:17.743Z'
	const createdDT = (new Date(article.createdAt)).toLocaleString();
	let publishedDT = "";
	if(article.publishedAt != ""){
		publishedDT = (new Date(article.publishedAt)).toLocaleString();
	}

	const handleEdit = (e: React.MouseEvent<HTMLButtonElement>) => {
		e.preventDefault();
		//route to edit
		navigate('/m/posts/edit', { state: {articleId: article.articleId }});
	};


	return (
		<>
			<tr>
				<th>{article.title}</th>
				<td>{article.status}</td>
				<td>{createdDT}</td>
				<td>{publishedDT}</td>
				<td><button className="btn btn-outline btn-primary" onClick={handleEdit}>Edit</button></td>
			</tr>
		</>
	)
}

export default PostListItem