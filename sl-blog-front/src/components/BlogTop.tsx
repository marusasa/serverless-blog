import './BlogTop.css'

function BlogTop({title, subTitle}:{title:string, subTitle: string}) {

  return (
    <>
		<div className="flex flex-row">
			<div className="grow">
				<h1 className="text-5xl pb-5"><a href="/">{title}</a></h1>
				<h2 className="text-base md:text-3xl pb-3">{subTitle}</h2>
			</div>
		</div>
    </>
  )
}

export default BlogTop