import { API_URL, GET_DEFAULT_PROFILE_IMAGE } from "../config";
import useAuthStore from "../store/useAuthStore";

const Header = () => {
  const { user } = useAuthStore();

  const onClick = () => {
    if (user) {
      // TODO
    } else {
      window.location.href = `${API_URL}/oauth2/authorization/google?redirect_environment=local`;
    }
  };

  return (
    <header className="w-full h-[80px] flex items-center justify-end">
      <button
        className="rounded-sm bg-white px-[12px] py-[6px] hover:bg-gray duration-fast"
        onClick={onClick}
      >
        {user ? (
          <div className="flex font-medium text-mmd">
            <img
              src={user.user.profileImage ?? GET_DEFAULT_PROFILE_IMAGE()}
              alt="user"
              className="w-[24px] h-[24px] mr-[6px] object-cover"
            />
            {user.user.name}
          </div>
        ) : (
          <div className="flex font-medium text-mmd">
            <img
              src="/icons/google.svg"
              alt="user"
              className="w-[24px] h-[24px] mr-[6px]"
            />
            <div className="pr-[6px]">구글 로그인</div>
          </div>
        )}
      </button>
    </header>
  );
};

export default Header;
