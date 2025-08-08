import { api, clearAuth, setAuth } from '@/utils/api';
import { ApiError } from '@/utils/apiError';
import { jwtDecode } from 'jwt-decode';
import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from 'react';
import { useNavigate } from 'react-router';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const jwtRef = useRef(localStorage.getItem('jwt'));

  const navigate = useNavigate();

  const [account, setAccount] = useState(() => {
    const jwt = localStorage.getItem('jwt');
    if (jwt) {
      let decodedJwt;
      try {
        decodedJwt = jwtDecode(jwt);
      } catch {
        localStorage.removeItem('jwt');
        clearAuth();
        return null;
      }
      if (decodedJwt.exp * 1000 <= Date.now()) {
        return null;
      }
      return decodedJwt;
    }
    return null;
  });

  const register = useCallback(async ({ email, password }) => {
    return await api.post('/auth/register', {
      email,
      password,
    });
  }, []);

  const login = useCallback(
    async (data) => {
      try {
        const response = await api.post('auth/token', data);
        const jwt = response.data.data;
        let decodedJwt;
        try {
          decodedJwt = jwtDecode(jwt);
        } catch {
          logout();
          return;
        }
        localStorage.setItem('jwt', jwt);
        jwtRef.current = jwt;
        setAccount(decodedJwt);
        setAuth(jwt);

        const lastPath = localStorage.getItem('lastPath');
        if (lastPath) {
          localStorage.removeItem('lastPath');
          navigate(lastPath);
        } else {
          navigate('/');
        }
      } catch (error) {
        const errorMessage =
          error?.response?.data?.message ?? error?.message ?? 'Unknown error';
        const errorStatus = error?.response?.status;
        throw new ApiError(errorMessage, errorStatus);
      }
    },
    [navigate]
  );

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    if (!jwt) return;

    const lastPath = window.location.pathname;
    const handleJwtExpiry = () => {
      localStorage.setItem('lastPath', lastPath);
      logout();
      navigate('/login');
    };

    let decodedJwt;
    try {
      decodedJwt = jwtDecode(jwt);
    } catch {
      logout();
      return;
    }
    const jwtExpiry = decodedJwt.exp * 1000 - Date.now();
    if (jwtExpiry <= 0) {
      handleJwtExpiry();
      return;
    }

    const timeout = setTimeout(() => {
      handleJwtExpiry();
    }, jwtExpiry);

    const interval = setInterval(() => {
      const checkedJwt = jwtRef.current; // reikia stebeti busena atmintyje, nes localStorage.getItem("jwt") = letas sinchroninis I/O veiksmas. Vykstant logout setInterval gali vistiek, nes gali gali ivykti tuo pat laiku kaip ir logout/tarp navigate, nespejus istrinti jwt is localstorage!!.
      if (!checkedJwt) return;

      let decodedCheckedJwt;
      try {
        decodedCheckedJwt = jwtDecode(checkedJwt);
      } catch {
        clearTimeout(timeout);
        clearInterval(interval);
        logout();
      }
      const checkedJwtExpiry = decodedCheckedJwt.exp * 1000 - Date.now();
      if (checkedJwtExpiry <= 0) {
        clearTimeout(timeout);
        handleJwtExpiry();
      }
    }, 45000);

    return () => {
      clearTimeout(timeout);
      clearInterval(interval);
    };
  }, []);

  const logout = useCallback(() => {
    setAccount(null);
    clearAuth();
    jwtRef.current = null;
    localStorage.removeItem('jwt');
  }, []);

  const isLoggedIn = !!account;

  return (
    <AuthContext.Provider
      value={{ register, login, logout, account, isLoggedIn }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
