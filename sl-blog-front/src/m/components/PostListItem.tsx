import { PostType } from '../../types/MyTypes';
import { useNavigate } from "react-router-dom";

function PostListItem({ article }: { article: PostType }) {

	const navigate = useNavigate();

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
				<td><button className="btn btn-outline btn-primary" onClick={handleEdit}>Edit</button></td>
			</tr>
		</>
	)
}

export default PostListItem