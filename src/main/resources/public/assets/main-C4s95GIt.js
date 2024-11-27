import { j as jsxRuntimeExports, r as reactExports, C as Constants, L as Loading, O as Outlet, a as Link, u as useOutletContext, b as useParams, M as Markdown, c as remarkGfm, d as createBrowserRouter, e as createRoot, R as RouterProvider } from "./index-BfZCrIl1.js";
function BlogTop({ title, subTitle }) {
  return /* @__PURE__ */ jsxRuntimeExports.jsx(jsxRuntimeExports.Fragment, { children: /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "flex flex-row", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "grow", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("h1", { className: "text-5xl pb-5", children: /* @__PURE__ */ jsxRuntimeExports.jsx("a", { href: "/", children: title }) }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { className: "text-base md:text-3xl pb-3", children: subTitle })
  ] }) }) });
}
function ViewCompProfilePic({ url, sizeClass }) {
  return /* @__PURE__ */ jsxRuntimeExports.jsx(jsxRuntimeExports.Fragment, { children: /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "avatar mb-5 w-full place-content-center", children: /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: `${sizeClass} rounded shadow-lg `, children: /* @__PURE__ */ jsxRuntimeExports.jsx("img", { src: url }) }) }) });
}
function ViewCompListList({ title, nameValList }) {
  const list = nameValList.map((item) => {
    return /* @__PURE__ */ jsxRuntimeExports.jsxs("li", { className: "mb-1", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "a",
        {
          href: `${item.url}`,
          className: "link link-accent",
          target: "_blank",
          children: item.name
        }
      ),
      " - ",
      item.desc
    ] });
  });
  return /* @__PURE__ */ jsxRuntimeExports.jsx(jsxRuntimeExports.Fragment, { children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "shadow-lg bg-teal-100 text-left	p-3 mb-5", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "text-center mb-2", children: title }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("ul", { children: list })
  ] }) });
}
function ViewCompTextBox({ title, text }) {
  const replaceMarkdownLinks = (markdownText) => {
    const regex = /\[([^\]]+)\]\(([^)]+)\)/g;
    const parts = markdownText.split(regex);
    let skip = false;
    return parts.map((part, index) => {
      if (index % 3 === 1 && index + 1 < parts.length) {
        const linkText = part;
        const linkUrl = parts[index + 1];
        skip = true;
        return /* @__PURE__ */ jsxRuntimeExports.jsx("a", { href: linkUrl, target: "_blank", className: "link link-accent", children: linkText }, index);
      }
      if (skip) {
        skip = false;
        return "";
      } else {
        return part;
      }
    });
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsx(jsxRuntimeExports.Fragment, { children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "shadow-lg bg-teal-100 text-left	p-3 mb-5", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "text-center mb-2", children: title }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "whitespace-pre-wrap", children: replaceMarkdownLinks(text) })
  ] }) });
}
function SideContents() {
  const [componentList, setComponentList] = reactExports.useState("");
  reactExports.useEffect(() => {
    fetch("/components").then((response) => response.json()).then((data) => {
      if (data.result == "success") {
        const components = data.components;
        const list = components.map((item) => {
          switch (item.type) {
            case Constants.VIEW_COMPONENT_TYPE_PROFILE_PIC: {
              const json = JSON.parse(item.json);
              return /* @__PURE__ */ jsxRuntimeExports.jsx(ViewCompProfilePic, { url: json.url, sizeClass: `pp-w-${json.size}` });
            }
            case Constants.VIEW_COMPONENT_TYPE_LINK_LIST: {
              const json = JSON.parse(item.json);
              return /* @__PURE__ */ jsxRuntimeExports.jsx(ViewCompListList, { title: json.title, nameValList: json.items });
            }
            case Constants.VIEW_COMPONENT_TYPE_TEXT_BOX: {
              const json = JSON.parse(item.json);
              return /* @__PURE__ */ jsxRuntimeExports.jsx(ViewCompTextBox, { title: json.title, text: json.text });
            }
            default: {
              console.log("View Component Type not supported: " + item.type);
              break;
            }
          }
        });
        setComponentList(list);
      } else {
        alert(JSON.stringify(data.messages));
      }
    }).catch((err) => {
      console.log(err.message);
      alert("Failed to load componetns.");
    });
  }, []);
  return /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "py-4", children: componentList }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "py-4 text-center text-slate-400", children: /* @__PURE__ */ jsxRuntimeExports.jsx("a", { href: "/m/", children: /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "btn btn-ghost btn-sm m-2", children: "Manage" }) }) })
  ] });
}
const _AnalyticUtil = class _AnalyticUtil {
  static init(visitorId) {
    this.visitorId = visitorId;
    setInterval(function() {
      if (_AnalyticUtil.currentPath != window.location.pathname) {
        if (_AnalyticUtil.currentPath !== "") {
          _AnalyticUtil.sendEvent(visitorId, _AnalyticUtil.currentPath, "hidden");
        }
        _AnalyticUtil.currentPath = window.location.pathname;
        _AnalyticUtil.sendEvent(visitorId, window.location.pathname, document.visibilityState);
        _AnalyticUtil.hiddenVisibleSent = false;
        _AnalyticUtil.timeOnPage = 0;
      } else {
        if (_AnalyticUtil.hiddenVisibleSent === false && _AnalyticUtil.lastSentStatus == "visible" && document.visibilityState === "visible") {
          _AnalyticUtil.timeOnPage = _AnalyticUtil.timeOnPage + _AnalyticUtil.INTERVAL;
          if (_AnalyticUtil.timeOnPage >= 1e4) {
            _AnalyticUtil.sendEvent(visitorId, _AnalyticUtil.currentPath, "hidden&visible");
            _AnalyticUtil.hiddenVisibleSent = true;
          }
        }
      }
    }, _AnalyticUtil.INTERVAL);
    document.onvisibilitychange = () => {
      _AnalyticUtil.sendEvent(visitorId, _AnalyticUtil.currentPath, document.visibilityState);
    };
  }
  static sendEvent(visitorId, path, state) {
    console.log(/* @__PURE__ */ new Date() + " " + visitorId + " " + path + " " + state);
    fetch("/analytics", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        visitorId,
        path,
        state
      }),
      keepalive: true
    }).catch((error) => {
      console.error(error);
    });
    _AnalyticUtil.lastSentStatus = state;
  }
};
_AnalyticUtil.currentPath = "";
_AnalyticUtil.visitorId = 0;
_AnalyticUtil.timeOnPage = 0;
_AnalyticUtil.INTERVAL = 1e3;
_AnalyticUtil.hiddenVisibleSent = false;
_AnalyticUtil.lastSentStatus = "";
let AnalyticUtil = _AnalyticUtil;
function App() {
  const [title, setTitle] = reactExports.useState("");
  const [subTitle, setSubTitle] = reactExports.useState("");
  const [loaded, setLoaded] = reactExports.useState(false);
  const [showAboutMe, setShowAboutMe] = reactExports.useState(false);
  const [iconUrl, setIconUrl] = reactExports.useState("");
  const [visitorId, setVisitorId] = reactExports.useState(0);
  reactExports.useEffect(() => {
    fetch("/basic-info").then((response) => response.json()).then((data) => {
      if (data.result == "success") {
        setTitle(data.title);
        document.title = data.title;
        setSubTitle(data.subTitle);
        setLoaded(true);
        setIconUrl(data.iconUrl);
        if (data.faviconUrl != "") {
          addFavicon(data.faviconUrl);
        }
        setVisitorId(data.visitorId);
        AnalyticUtil.init(data.visitorId);
      } else {
        alert(JSON.stringify(data.messages));
      }
      console.log(data);
    }).catch((err) => {
      console.log(err.message);
      alert("Failed to load articles.");
    });
  }, []);
  const toggleShowAboutMe = (e) => {
    e.preventDefault();
    setShowAboutMe(!showAboutMe);
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsx(jsxRuntimeExports.Fragment, { children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "container mx-auto font-mono", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx(Loading, { loaded }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: loaded ? "visible" : "invisible", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "columns-1 text-left", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx(BlogTop, { title, subTitle }),
        /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "flex md:hidden", children: [
          /* @__PURE__ */ jsxRuntimeExports.jsx(ShowIcon, { iconUrl }),
          /* @__PURE__ */ jsxRuntimeExports.jsx(
            "a",
            {
              className: "link link-accent mt-2",
              onClick: toggleShowAboutMe,
              children: showAboutMe ? "Back to Posts" : "Show About Me"
            }
          )
        ] })
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "grid grid-cols-6", children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: showAboutMe ? " hidden " : " col-span-6 md:col-span-4", children: /* @__PURE__ */ jsxRuntimeExports.jsx(Outlet, { context: [loaded, setLoaded] }) }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: showAboutMe ? " col-span-6 " : " hidden   md:col-span-2 md:block md:pl-4", children: /* @__PURE__ */ jsxRuntimeExports.jsx(SideContents, {}) })
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { children: /* @__PURE__ */ jsxRuntimeExports.jsxs("p", { className: "text-slate-400 text-center", children: [
        "Powered by ",
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "a",
          {
            className: "link",
            target: "_blank",
            href: "https://github.com/marusasa/serverless-blog",
            children: "Serverless Blog Project"
          }
        )
      ] }) })
    ] })
  ] }) });
}
function ShowIcon({ iconUrl }) {
  if (iconUrl != "") {
    return /* @__PURE__ */ jsxRuntimeExports.jsx("img", { src: iconUrl, className: "w-10 rounded-full mr-4 shadow-lg" });
  } else {
    return /* @__PURE__ */ jsxRuntimeExports.jsx(
      "svg",
      {
        xmlns: "http://www.w3.org/2000/svg",
        fill: "none",
        viewBox: "0 0 24 24",
        strokeWidth: 1.5,
        stroke: "currentColor",
        className: "size-10 mr-4 shadow-lg rounded-full",
        children: /* @__PURE__ */ jsxRuntimeExports.jsx(
          "path",
          {
            strokeLinecap: "round",
            strokeLinejoin: "round",
            d: "M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z"
          }
        )
      }
    );
  }
}
function addFavicon(href) {
  const link = document.createElement("link");
  link.rel = "shortcut icon";
  link.type = "image/png";
  link.href = href;
  document.head.appendChild(link);
}
function PostsItem({ article }) {
  const pubDateYYYYMMDD = article.publishedAt.substring(0, 10);
  const pubDate = new Date(article.publishedAt);
  const dateText = pubDate.toLocaleDateString();
  const link = convertToAllowedChars(article.title) + "_" + article.articleId;
  const dialogRef = reactExports.useRef(null);
  const handleShowSummary = (e) => {
    e.preventDefault();
    dialogRef.current.showModal();
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "card-body p-0", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { className: "card-title", children: article.title }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "font-thin italic", children: /* @__PURE__ */ jsxRuntimeExports.jsx("time", { dateTime: pubDateYYYYMMDD, children: dateText }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "whitespace-pre-wrap line-clamp-4 mb-3", children: article.body }),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("p", { children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx(Link, { to: "/post/" + link, className: "link link-accent mr-5", children: "Read More..." }),
        /* @__PURE__ */ jsxRuntimeExports.jsx(
          "a",
          {
            className: article.summary == "" || article.summary == null ? "invisible" : "visible link link-accent",
            onClick: handleShowSummary,
            children: "Show AI Summary"
          }
        )
      ] })
    ] }) }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("dialog", { id: "my_modal_1", className: "modal", ref: dialogRef, children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "modal-box", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { className: "font-bold text-lg", children: "AI Summary:" }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "py-4", children: article.summary }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "modal-action", children: /* @__PURE__ */ jsxRuntimeExports.jsx("form", { method: "dialog", children: /* @__PURE__ */ jsxRuntimeExports.jsx("button", { className: "btn", children: "OK" }) }) })
    ] }) })
  ] });
}
function convertToAllowedChars(str) {
  str = str.replaceAll(" ", "-");
  const allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-";
  const regex = new RegExp(`[^${allowedCharacters}]`, "g");
  return str.replace(regex, "");
}
function Posts() {
  const [articles, setArticles] = reactExports.useState([]);
  const [parentLoaded, setParentLoaded] = useOutletContext();
  const [loaded, setLoaded] = reactExports.useState(false);
  const [hasMore, setHasMore] = reactExports.useState(true);
  const [pagingArray, setPagingArray] = reactExports.useState([0]);
  const [pageNum, setPageNum] = reactExports.useState(1);
  const [pageTotal, setPageTotal] = reactExports.useState(0);
  const loadData = () => {
    setLoaded(false);
    fetch("/articles/page/" + pagingArray[pageNum - 1]).then((response) => response.json()).then((data) => {
      if (data.result == "success") {
        setLoaded(true);
        setArticles(data.articles);
        setHasMore(data.hasMore);
        if (pagingArray.length == pageNum) {
          pagingArray.push(data.lastQueryVal);
          setPagingArray(pagingArray);
        }
        if (pageNum == 1) {
          setPageTotal(data.pageTotal);
        }
        window.scrollTo({ top: 0, behavior: "smooth" });
      } else {
        alert(JSON.stringify(data.messages));
      }
      console.log(data);
    }).catch((err) => {
      console.log(err.message);
      alert("Failed to load articles.");
    });
  };
  reactExports.useEffect(() => {
    loadData();
  }, [pageNum]);
  const next = () => {
    setPageNum(pageNum + 1);
  };
  const prev = () => {
    setPageNum(pageNum - 1);
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsxs(jsxRuntimeExports.Fragment, { children: [
    /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "py-4", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(Loading, { loaded: parentLoaded ? loaded : true }),
      articles.map((a) => {
        return /* @__PURE__ */ jsxRuntimeExports.jsx(PostsItem, { article: a }, a.articleId);
      })
    ] }),
    /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "flex flex-col items-center pb-4 " + (loaded ? "" : "hidden"), children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "button",
        {
          className: "btn btn-secondary btn-sm mr-3 px-6 " + (pageNum == 1 ? " btn-disabled " : ""),
          onClick: prev,
          children: "<"
        }
      ),
      /* @__PURE__ */ jsxRuntimeExports.jsxs("span", { children: [
        "Page: ",
        pageNum,
        "/",
        pageTotal
      ] }),
      /* @__PURE__ */ jsxRuntimeExports.jsx(
        "button",
        {
          className: "btn btn-secondary btn-sm ml-3 px-6 " + (hasMore ? " " : " btn-disabled "),
          onClick: next,
          children: ">"
        }
      )
    ] }) })
  ] });
}
function PostPage() {
  const params = useParams();
  const [article, setArticle] = reactExports.useState({});
  const [pubDateYYYYMMDD, setPubDateYYYYMMDD] = reactExports.useState("");
  const [dateText, setDateText] = reactExports.useState("");
  const refArticleP = reactExports.useRef(null);
  const [loaded, setLoaded] = reactExports.useState(false);
  const [likeCount, setLikeCount] = reactExports.useState(0);
  const [processLike, setProcessLike] = reactExports.useState(false);
  const [likeClicked, setLikeClicked] = reactExports.useState(false);
  reactExports.useEffect(() => {
    let postId = params.postId;
    const index = params.postId.indexOf("#");
    if (index > -1) {
      postId = postId.substring(0, index);
    }
    fetch("/articles/" + postId).then((response) => response.json()).then((data) => {
      if (data.result == "success") {
        setArticle(data.article);
        setLikeCount(data.article.likes);
        setPubDateYYYYMMDD(data.article.publishedAt.substring(0, 10));
        const pubDate = new Date(data.article.publishedAt);
        setDateText(pubDate.toLocaleDateString());
        setLoaded(true);
      } else {
        alert("Post not loaded...");
      }
    }).catch((err) => {
      console.log(err.message);
      alert("Failed to load article.");
    }).finally(() => {
      refArticleP.current.addEventListener("click", handleArticleClick);
    });
  }, []);
  const handleArticleClick = (event) => {
    if (event.target.tagName === "A") {
      if (event.target.getAttribute("href").toLowerCase().startsWith("http")) {
        event.preventDefault();
        window.open(event.target.href, "_blank");
      }
    }
  };
  const handleLike = (e) => {
    e.preventDefault();
    setProcessLike(true);
    fetch("/articles/" + article.articleId + "/like", {
      method: "PATCH",
      headers: {
        "Content-type": "application/json; charset=UTF-8"
      }
    }).then((response) => response.json()).then((data) => {
      if (data.result == "success") {
        setLikeClicked(true);
        setLikeCount(data.count);
      } else {
        alert(JSON.stringify(data.messages));
      }
    }).catch((err) => {
      alert("Failed to like.");
      console.log(err.message);
    }).finally(() => {
      setProcessLike(false);
    });
  };
  const generateSlug = (input) => {
    let str = input.replace(/^\s+|\s+$/g, "");
    str = str.toLowerCase();
    str = str.replace(/[^a-z0-9 -]/g, "").replace(/\s+/g, "-").replace(/-+/g, "-");
    return str;
  };
  const components = {
    h1: ({ children }) => /* @__PURE__ */ jsxRuntimeExports.jsx("h1", { id: generateSlug(children), children }),
    h2: ({ children }) => /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { id: generateSlug(children), children }),
    h3: ({ children }) => /* @__PURE__ */ jsxRuntimeExports.jsx("h3", { id: generateSlug(children), children })
  };
  return /* @__PURE__ */ jsxRuntimeExports.jsx(jsxRuntimeExports.Fragment, { children: /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "py-4", children: /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "card bg-white w-full mb-8 text-left shadow-lg border-solid border border-slate-200 p-4 md:p-8", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("div", { className: "card-body p-0", children: [
    /* @__PURE__ */ jsxRuntimeExports.jsx(Loading, { loaded }),
    /* @__PURE__ */ jsxRuntimeExports.jsxs("article", { className: loaded ? "visible" : "invisible", children: [
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { children: /* @__PURE__ */ jsxRuntimeExports.jsx(Link, { to: "/", className: "link link-accent", children: "Blog Top" }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("h2", { className: "card-title", children: article.title }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "font-thin italic", children: /* @__PURE__ */ jsxRuntimeExports.jsx("time", { dateTime: pubDateYYYYMMDD, children: dateText }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("div", { className: "mb-3", children: /* @__PURE__ */ jsxRuntimeExports.jsxs("button", { className: "btn btn-xs btn-ghost", onClick: handleLike, children: [
        /* @__PURE__ */ jsxRuntimeExports.jsx("svg", { xmlns: "http://www.w3.org/2000/svg", fill: likeClicked ? "oklch(var(--er))" : "white", viewBox: "0 0 24 24", strokeWidth: 1, stroke: "currentColor", className: "size-6", children: /* @__PURE__ */ jsxRuntimeExports.jsx("path", { strokeLinecap: "round", strokeLinejoin: "round", d: "M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12Z" }) }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: "text-base " + (processLike ? "hidden" : ""), children: likeCount }),
        /* @__PURE__ */ jsxRuntimeExports.jsx("span", { className: (processLike ? "" : "hidden ") + " loading loading-spinner" })
      ] }) }),
      /* @__PURE__ */ jsxRuntimeExports.jsx("p", { className: "prose max-w-none", ref: refArticleP, children: /* @__PURE__ */ jsxRuntimeExports.jsx(
        Markdown,
        {
          className: "reactMarkDown",
          remarkPlugins: [remarkGfm],
          components,
          children: article.body
        }
      ) })
    ] })
  ] }) }) }) });
}
if (location.hostname != "localhost" && location.protocol !== "https:") {
  location.replace(`https:${location.href.substring(location.protocol.length)}`);
}
const router = createBrowserRouter([
  {
    path: "/",
    element: /* @__PURE__ */ jsxRuntimeExports.jsx(App, {}),
    children: [
      {
        index: true,
        element: /* @__PURE__ */ jsxRuntimeExports.jsx(Posts, {})
      },
      {
        path: "post/:postId",
        element: /* @__PURE__ */ jsxRuntimeExports.jsx(PostPage, {})
      }
    ]
  }
]);
createRoot(document.getElementById("root")).render(
  /* @__PURE__ */ jsxRuntimeExports.jsx(reactExports.StrictMode, { children: /* @__PURE__ */ jsxRuntimeExports.jsx(RouterProvider, { router }) })
);
