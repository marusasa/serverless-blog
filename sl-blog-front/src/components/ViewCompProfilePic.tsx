import './ViewCompProfilePic.css'

function ViewCompProfilePic({url, sizeClass}:{url:string, sizeClass:string}) {

	return (
		<>
			<div className="avatar mb-5 w-full place-content-center">
				<div className={`${sizeClass} rounded shadow-lg `}>
					<img src={url}  />
				</div>
			</div>

		</>
	)
}

export default ViewCompProfilePic