import { useEffect , useRef, useState} from 'react';
import Loading from "../components/Loading";
import {ImageItem} from "../../types/MyTypes";

function ImageManager({ isOpen, onClose, articleId }:{isOpen:boolean, onClose:(e: React.FormEvent) => void, articleId:string}) {
	const dialogRef = useRef(null);
	const fileInputRef = useRef(null);
	const [selectedFiles, setSelectedFiles] = useState([]);
	const [images, setImages] = useState([]);
	const [loaded, setLoaded] = useState(false);
	

	useEffect(() => {
		if(isOpen){
			dialogRef.current.showModal();		
			loadImages();	
		}else{
			dialogRef.current.close();
		}
	}, [isOpen]);
	
	
	const loadImages = () => {
		fetch('/mng/articles/' + articleId + '/images', {
			method: 'GET'
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setImages(data.images);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				alert('Error loading images.');
				console.log(err.message);
			})
			.finally(() => {
				setLoaded(true);
			});		
	}
	
	const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		setSelectedFiles(event.target.files);
	};
	
	const uploadImage = async (e: React.FormEvent) => {
		e.preventDefault();

		if (selectedFiles.length > 0) {
			const formData = new FormData();
			for (let i = 0; i < selectedFiles.length; i++) {
				formData.append('files[]', selectedFiles[i]); // Use the same key for all files
			}

			await fetch('/mng/articles/' + articleId + '/images', {
				method: 'POST',
				body: formData
			})
				.then((response) => response.json())
				.then((data) => {
					if (data.result == 'success') {
						setLoaded(false);
						loadImages();
						fileInputRef.current.value = '';
					} else {
						alert(JSON.stringify(data.messages));
					}
				})
				.catch((err) => {
					alert('Failed to save image.');
					console.log(err.message);
				})

		}else{
			alert('File not selected.');
		}
	};
		
	const urlToClipboard = (image: ImageItem) => {
		navigator.clipboard.writeText('![' + image.fileName + '](' + encodeURI(image.url) + ')');
		onClose();
	}
	
	return (
		<dialog id="my_modal_3" className="modal" ref={dialogRef}>
			<div className="modal-box">
				<button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2" 
						onClick={onClose}>✕</button>
				<div>
					<h3 className="font-bold text-lg mb-3">Add Image</h3>
					<input type="file" name="files" onChange={handleFileChange} multiple ref={fileInputRef} className="mb-5"/>
					<button className="btn btn-sm" onClick={uploadImage}>Upload</button>
					<h3 className="font-bold text-lg  mb-3">Image List</h3>
					<Loading loaded={loaded}/>
					<div className={loaded?'':'hidden'}>
						<table>
							<thead>
								<th>Image File</th>
								<th></th>
							</thead>
							<tbody>
								{
									images.map((image: ImageItem) => {
										return (
											<>
												<tr>
													<td>{image.fileName}</td>
													<td><button className="btn btn-sm m-2" onClick={() => urlToClipboard(image)}>Copy URL Code</button></td>
												</tr>
											</>
										);
									})
								}
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</dialog>
	)
}

export default ImageManager