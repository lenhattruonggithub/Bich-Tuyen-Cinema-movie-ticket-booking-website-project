package com.example.jav_projecto1.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.jav_projecto1.dto.LoginResponse;
import com.example.jav_projecto1.entities.*;
import com.example.jav_projecto1.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jav_projecto1.dto.AccountDTO;
import com.example.jav_projecto1.dto.RegisterRequest;
import com.example.jav_projecto1.enumm.Role_enum;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    public Account save(Account acc) {
    return accRepository.save(acc);
    }

    public Account saveByRequest(RegisterRequest registerRequest, Role_enum role) {
        if (accRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + registerRequest.getUsername() + "' is already taken!");
        }

        Role role2 = roleRepository.findByRoleName(role);
        Account newAccount = Account.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .email(registerRequest.getEmail())
                .name(registerRequest.getName())
                .birthday(registerRequest.getBirthday())
                .gender(registerRequest.getGender())
                .identityCard(registerRequest.getIdentityCard())
                .phoneNumber(registerRequest.getPhoneNumber())
                .address(registerRequest.getAddress())
                .registerDate(LocalDate.now()) // setting registration date as current date
                .status(true) // setting a default status
                .role(role2)
                .build();

        return accRepository.save(newAccount);
    }

    public Optional<Account> login(String username, String password) {
        Optional<Account> acc_optn = accRepository.findByUsername(username);

        if (acc_optn.isPresent()) {
            Account acc = acc_optn.get();
            if (acc.getPassword().equals(password)) {
                return Optional.of(acc);
            }
        }

        return Optional.empty();
    }

    public Account registerUser(Account newacc) {
        if (accRepository.findByUsername(newacc.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username has been already taken!");
        }
        return accRepository.save(newacc);
    }

    public List<Account> getAllMemberAccounts() {
        return accRepository.findByRole_RoleName(Role_enum.MEMBER);
    }

    public List<AccountDTO> getAllMemberAndBannedAccountDTOs() {
        return accRepository.findByRole_RoleNameIn(List.of(Role_enum.MEMBER, Role_enum.BANNED))
                .stream()
                .map(AccountService::toDTO)
                .sorted((a1, a2) -> {
                    int roleOrder1 = getRoleOrder(a1.getRole());
                    int roleOrder2 = getRoleOrder(a2.getRole());
                    if (roleOrder1 != roleOrder2) {
                        return Integer.compare(roleOrder1, roleOrder2);
                    }
                    return a1.getName().compareToIgnoreCase(a2.getName());
                })
                .collect(Collectors.toList());
    }

    public Account toggleBanStatus(Long accountId, boolean setBanned) {
        logger.info("Setting ban status for accountId={} to {}", accountId, setBanned);
        Optional<Account> opt = accRepository.findById(accountId);
        if (opt.isEmpty())
        {
            logger.error("Account with ID {} not found", accountId);
            throw new RuntimeException("Account not found");
        }

        Account account = opt.get();
        account.setStatus(!setBanned); // Toggle status based on ban state
        if (setBanned) {
            account.setRole(roleRepository.findByRoleName(Role_enum.BANNED));
        }
        else {
            account.setRole(roleRepository.findByRoleName(Role_enum.MEMBER));
        }

        logger.info("Ban status for accountId={} set to {}", accountId, setBanned);
        return accRepository.save(account);
    }

    public Account updateProfile(Long accountId, AccountDTO dto) {
        Optional<Account> opt = accRepository.findById(accountId);
        if (opt.isEmpty()) throw new RuntimeException("Account not found");
        Account acc = opt.get();
        acc.setName(dto.getName());
        acc.setAddress(dto.getAddress());
        acc.setBirthday(dto.getBirthday());
        acc.setGender(dto.getGender());
        acc.setImage(dto.getImage());
        // Do not update username, password, email,phone number or role here
        return accRepository.save(acc);
    }

    public Account updateProfileWithSession(Long accountId, AccountDTO dto, HttpSession session) {
        Account updated = updateProfile(accountId, dto);
        session.setAttribute("userLogin", updated);
        logger.info("Profile updated for user id={}", updated.getAccountId());
        return updated;
    }

    public String changePasswordWithSession(Map<String, String> req, HttpSession session) {
        Object userObj = session.getAttribute("userLogin");
        if (userObj == null) {
            logger.warn("Unauthorized password change attempt: no user in session id={}", session.getId());
            throw new IllegalArgumentException("Not logged in");
        }
        Account user = (Account) userObj;
        String oldPassword = req.get("oldPassword");
        String newPassword = req.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Missing password fields");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(newPassword);
        save(user);
        session.setAttribute("userLogin", user);
        logger.info("Password changed for user id={}", user.getAccountId());
        return "Password changed successfully";
    }

    public static AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        return AccountDTO.builder()
                .accountId(account.getAccountId())
                .username(account.getUsername())
                .email(account.getEmail())
                .name(account.getName())
                .birthday(account.getBirthday())
                .gender(account.getGender())
                .identityCard(account.getIdentityCard())
                .phoneNumber(account.getPhoneNumber())
                .address(account.getAddress())
                .registerDate(account.getRegisterDate())
                .status(account.getStatus())
                .role(account.getRole() != null ? account.getRole().getRoleName().name() : null)
                .image(account.getImage())
                .build();
    }


    private static int getRoleOrder(String role) {
        if ("BANNED".equalsIgnoreCase(role)) return 0;
        if ("MEMBER".equalsIgnoreCase(role)) return 1;
        return 2; // anything else after
    }

    public Optional<Account> findByEmail(String email) {
        return accRepository.findByEmail(email);
    }

    public void delete(Account account) {
        List<Invoice> invoices = account.getInvoices();
        for (Invoice inv : invoices) {
            inv.setAccount(null);
            logger.info("Clearing invoice with ID={}", inv.getInvoiceId());
            invoiceRepository.save(inv);
        }

        Long accountId = account.getAccountId();
        Optional<Member> member = memberRepository.findByAccount_AccountId(accountId);
        if (member.isPresent())
        {
            logger.info("Removing member with ID={}", accountId);
            memberRepository.delete(member.get());
        }

        Optional<Employee> employee = employeeRepository.findByAccount_AccountId(accountId);
        if (employee.isPresent())
        {
            logger.info("Removing employee with ID={}", accountId);
            employeeRepository.delete(employee.get());
        }

        logger.info("Removing account with ID={}", accountId);
        accRepository.delete(account);
    }

    public LoginResponse toLoginResponse(Account account) {
        if (account == null) return null;
        return LoginResponse.builder()
                .accountId(account.getAccountId())
                .username(account.getUsername())
                .email(account.getEmail())
                .name(account.getName())
                .role(account.getRole() != null ? account.getRole().getRoleName().name() : null)
                .phoneNumber(account.getPhoneNumber())
                .address(account.getAddress())
                .birthday(account.getBirthday())
                .gender(account.getGender())
                .build();
    }

    public LoginResponse handleRegister(RegisterRequest registerRequest, HttpSession session, MemberService memberService) {
        String code = registerRequest.getVerifyCode();
        String sessionCode = (String) session.getAttribute("verifyCode");
        String sessionEmail = (String) session.getAttribute("verifyEmail");

        if (sessionCode == null || sessionEmail == null ||
                !sessionEmail.equals(registerRequest.getEmail()) ||
                !sessionCode.equals(code)) {
            throw new IllegalArgumentException("Mã xác thực hoặc email không hợp lệ");
        }

        // Xóa mã xác thực sau khi dùng
        session.removeAttribute("verifyCode");
        session.removeAttribute("verifyEmail");

        Member member = memberService.saveByRequest(registerRequest);
        Account savedAcc = member.getAccount();
        if (savedAcc == null) {
            throw new IllegalArgumentException("Không thể tạo tài khoản");
        }
        session.setAttribute("userLogin", savedAcc);
        return toLoginResponse(savedAcc);
    }
}
