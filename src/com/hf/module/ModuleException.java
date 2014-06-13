package com.hf.module;

/**
 * @author Sean
 *
 */
public class ModuleException extends Exception{
	private static final long serialVersionUID = 1L;
	
	
	public static final int ERROR_CODE_UNKNOWN = 0;
	//common 
    public static final int ERROR_CODE_COMMOM_UNKNOWN_MESSAGE 				= -11; //未知
	public static final int ERROR_CODE_COMMOM_PRIVILEGE_ILLEGAL             = -12; //权限非法
    public static final int ERROR_CODE_COMMOM_SESSION_ID_ILLEGAL            = -13; //sid 
	public static final int ERROR_CODE_COMMOM_DATABASE_ACCESS_UNSUCCESSFUL 	= -14; 	//
    public static final int ERROR_CODE_COMMOM_RUNTIME_GENERAL_EXCEPTION     = -15;  //
    public static final int ERROR_CODE_COMMOM_MESSAGE_PATTERN_ILLEGAL       = -16;  //json 格式错误 
    public static final int ERROR_CODE_COMMOM_MESSAGE_INPUT_ILLEGAL         = -17; //json 参数
    
    public static final int ERROR_CODE_RECV_CMD_DECODE_ERR					= -32;
    public static final int ERROR_CODE_RECV_CMD_NULL_ERR					= -31;
    public static final int ERROR_CODE_PARAMETERS_OF_INPUT_ERROR			= -30;
    
	//security
    public static final int ERROR_CODE_SECURITY_PASSWORD_INVALID            = -101;
    public static final int ERROR_CODE_SECURITY_PASSWORD_EMPTY              = -102; 
    public static final int ERROR_CODE_SECURITY_USER_NOT_EXIST              = -103;
    public static final int ERROR_CODE_SECURITY_USER_ALREADY_EXIST          = -104;
    public static final int ERROR_CODE_SECURITY_USER_EMAIL_ALREADY_USED     = -105;
    public static final int ERROR_CODE_SECURITY_USER_PHONE_ALREADY_USED     = -106;
    public static final int ERROR_CODE_SECURITY_USER_ID_CARD_ALREADY_USED   = -107;
    public static final int ERROR_CODE_SECURITY_USER_NUMBER_THREDHOLD_ALARM = -108;
    public static final int ERROR_CODE_SECURITY_SEND_EMAIL_FAILED           = -109;
    public static final int ERROR_CODE_SECURITY_SEND_SMS_FAILED             = -110;
    public static final int ERROR_CODE_SECURITY_ACCOUNT_HAS_BEEN_ACTIVATED  = -111;
    public static final int ERROR_CODE_SECURITY_ACTIVATIONCODE_ILLEGAL      = -112;
    public static final int ERROR_CODE_SECURITY_ACCOUNT_HAS_NOT_BEEN_ACTIVATED  = -113;
    public static final int ERROR_CODE_SECURITY_CAPTCHA_ILLEGAL             = -114;
    
    //management
    public static final int ERROR_CODE_MANAGEMENT_MODULE_NUMBER_THREDHOLD_ALARM = -201;
    public static final int ERROR_CODE_MANAGEMENT_MODULE_HAS_ALREADY_BEEN_ADDED = -202;
    public static final int ERROR_CODE_MANAGEMENT_MODULE_NOT_EXIST              = -203;
    public static final int ERROR_CODE_MANAGEMENT_DATA_NOT_EXIST                = -204;
    public static final int ERROR_CODE_MANAGEMENT_MODULE_GROUP_NOT_EXIST        = -205;
    
    //module
    public static final int ERROR_CODE_MODULE_RESPONSE_TIMEOUT = -301;
    public static final int ERROR_CODE_MODULE_NOT_ONLINE       = -302;
    public static final int ERROR_CODE_MODULE_IS_NOT_IN_AN_ACCOUNT = -303; 
	
	private int errorCode = ERROR_CODE_UNKNOWN;
	
	public ModuleException() {
	    super();
    }

	public ModuleException(int errorCode) {
	    super();
	    this.errorCode = errorCode;
    }
	
	

	public ModuleException(String message, Throwable cause) {
	    super(message, cause);
    }
	public ModuleException(String message) {
	    super(message);
    }
	public ModuleException(Throwable cause) {
	    super(cause);
    }
	

	public ModuleException(int errorCode, String message, Throwable cause) {
	    super(message, cause);
	    this.errorCode = errorCode;
    }
	

	public ModuleException(int errorCode, String message) {
	    super(message);
	    this.errorCode = errorCode;
    }


	public ModuleException(int errorCode,Throwable cause) {
	    super(cause);
	    this.errorCode = errorCode;
    }
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
}
