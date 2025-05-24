import useAuthStore from "../store/useAuthStore";

const Header = () => {
  const { user } = useAuthStore();

  const onClick = () => {
    if (user) {
    } else {
      window.location.href = `${process.env.REACT_APP_API_URL}/oauth2/authorization/google`;
    }
  };

  return (
    <header className="w-full h-[80px] flex items-center justify-end">
      <button
        className="rounded-sm bg-white pl-[12px] pr-[18px] py-[6px] hover:bg-gray duration-fast"
        onClick={onClick}
      >
        {user ? (
          <div className="flex font-medium text-mmd">
            {user.user.isProfileComplete && (
              <img
                src={user.user.picture}
                alt="user"
                className="w-[24px] h-[24px] mr-[6px]"
              />
            )}
            {user.user.name}
          </div>
        ) : (
          <div className="flex font-medium text-mmd">
            <img
              src="/icons/google.svg"
              alt="user"
              className="w-[24px] h-[24px] mr-[6px]"
            />
            구글 로그인
          </div>
        )}
      </button>
    </header>
  );
};

export default Header;
