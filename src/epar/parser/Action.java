package epar.parser;

public class Action {

	private static final Action INIT = new Action(ActionType.INIT, null);

	private static final Action FINISH = new Action(ActionType.FINISH, null);

	private static final Action IDLE = new Action(ActionType.IDLE, null);

	public static Action init() {
		return INIT;
	}

	public static Action shift(String category) {
		return new Action(ActionType.SHIFT, category);
	}

	public static Action binary(String category) {
		return new Action(ActionType.BINARY, category);
	}

	public static Action unary(String category) {
		return new Action(ActionType.UNARY, category);
	}

	public static Action finish() {
		return FINISH;
	}

	public static Action idle() {
		return IDLE;
	}

	private static enum ActionType {
		INIT, SHIFT, BINARY, UNARY, FINISH, IDLE;

		public static ActionType fromString(String string) {
			switch (string) {
			case "INIT":
				return INIT;
			case "SHIFT":
				return SHIFT;
			case "BINARY":
				return BINARY;
			case "UNARY":
				return UNARY;
			case "FINISH":
				return FINISH;
			case "IDLE":
				return IDLE;
			default:
				throw new IllegalArgumentException("Not an action type: "
						+ string);
			}
		}
	};

	private final ActionType type;

	private final String category;

	private Action(ActionType type, String category) {
		this.type = type;
		this.category = category;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (category == null) {
			return type.toString();
		} else {
			return type.toString() + "-" + category;
		}
	}

	public static Action fromString(String string) {
		String[] parts = string.split("-", 2);

		if (parts.length == 1) {
			ActionType type = ActionType.fromString(string);

			if (type == ActionType.FINISH) {
				return finish();
			} else if (type == ActionType.IDLE) {
				return idle();
			} else {
				throw new IllegalArgumentException(type
						+ " action needs category argument");
			}
		} else {
			ActionType type = ActionType.fromString(parts[0]);

			if (type == ActionType.SHIFT) {
				return shift(parts[1]);
			} else if (type == ActionType.BINARY) {
				return binary(parts[1]);
			} else if (type == ActionType.UNARY) {
				return unary(parts[1]);
			} else
				throw new IllegalArgumentException(type
						+ " action does not take category argument");
		}
	}

}
