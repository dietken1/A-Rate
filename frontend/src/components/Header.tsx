const Header = () => (
  <header className="w-full h-[80px] flex items-center justify-end">
    <button className="flex font-medium text-mmd rounded-sm bg-white pl-[12px] pr-[18px] py-[6px] hover:bg-gray duration-fast">
      <img
        src="/icons/google.svg"
        alt="user"
        className="w-[24px] h-[24px] mr-[6px]"
      />
      구글 로그인
    </button>
  </header>
);

export default Header;
