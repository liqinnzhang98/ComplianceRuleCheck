import { Link } from 'react-router-dom';
import HeaderBackground from '../assets/background.jpg';
import Logo from '../assets/logo.svg';

function Header() {
    return (
        <div
            style={{ backgroundImage: 'url(' + HeaderBackground + ')' }}
            className="h-[34px] align-middle bg-cover p-[0.45em] px-3 flex"
        >
            <Link to="/" className="w-[100px]">
                <img src={Logo} className="h-full" alt="logo" />
            </Link>
            <p className="text-orange-200 font-brand leading-4 absolute top-[9px] left-[7.5em]">Compliance Center</p>
        </div>
    );
}

export default Header;
